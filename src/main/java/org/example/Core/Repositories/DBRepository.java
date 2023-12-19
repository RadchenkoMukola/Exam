package org.example.Core.Repositories;


import org.example.Core.Models.Date;
import org.example.Core.Models.Calendar;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRepository extends UnicastRemoteObject implements Repository {
    private Connection connection;

    public DBRepository(String url, String user, String password) throws RemoteException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

            String calendarsTable = "CREATE TABLE IF NOT EXISTS calendars (id SERIAL PRIMARY KEY, name varchar(30), year int)";
            String datesTable = "CREATE TABLE IF NOT EXISTS dates (id SERIAL PRIMARY KEY, calendar_id int, name varchar(30), prio int, CONSTRAINT fk_calendar FOREIGN KEY(calendar_id) REFERENCES calendars(id))";

            statement.execute(calendarsTable);
            statement.execute(datesTable);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countCalendars() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM calendars";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int countDates() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM dates";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void updateCalendar(Calendar calendar) {
        try {
            Calendar item = getCalendar(calendar.getId());
            if (item != null) {
                Statement statement = connection.createStatement();
                String query = "UPDATE calendars SET name='" + calendar.getName() + "', year=" + calendar.getYear() + " WHERE id=" + calendar.getId();
                statement.execute(query);

                for (Date date : item.getDates()) {
                    if (!calendar.hasDate(date)) {
                        deleteDate(date.getId());
                    }
                }
                for (Date date :calendar.getDates()) {
                    if (!item.hasDate(date)) {
                        insertDate(calendar.getId(), date);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateDate(Date date) {
        try {
            Statement statement = connection.createStatement();
            String query = "UPDATE dates SET name='" + date.getName() + "', prio=" + date.getPrio() + " WHERE id=" + date.getId();
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveDatetoCalendar(int dateId, int calendarId) {
        try {
            if (getCalendar(calendarId) != null) {
                Statement statement = connection.createStatement();
                String query = "UPDATE dates SET calendar_id=" + calendarId + " WHERE id=" + dateId;
                statement.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertCalendar(Calendar calendar) {
        try {
            Statement statement = connection.createStatement();
            String calendarQuery = "INSERT INTO calendars(name, year) VALUES('" + calendar.getName() + "'," + calendar.getYear() + ")";
            statement.execute(calendarQuery);

            String findQuery = "SELECT * FROM teams WHERE name='" + calendar.getName() + "' AND year=" + calendar.getYear();
            ResultSet resultSet = statement.executeQuery(findQuery);
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                for (Date player : calendar.getDates()) {
                    String playerQuery = "INSERT INTO players(team_id, name, age) VALUES(" + id + ",'" + player.getName() + "'," + player.getPrio() + ")";
                    statement.execute(playerQuery);
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot add calendar as it already exists");
        }
    }

    @Override
    public void insertDate(int calendarId, Date date) {
        try {
            Statement statement = connection.createStatement();
            String datesQuery = "INSERT INTO dates(calendar_id, name, prio) VALUES(" + calendarId + ",'" + date.getName() + "'," + date.getPrio() + ")";
            statement.execute(datesQuery);
        } catch (SQLException e) {
            System.out.println("Cannot add date as it already exists");
        }
    }

    @Override
    public void deleteCalendar(int id) {
        try {
            Statement statement = connection.createStatement();
            String datesQuery = "DELETE FROM dates WHERE calendar_id = " + id;
            statement.execute(datesQuery);
            String calendarQuery = "DELETE FROM calendars WHERE id = " + id;
            statement.execute(calendarQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDate(int id) {
        try {
            Statement statement = connection.createStatement();
            String datesQuery = "DELETE FROM dates WHERE id = " + id;
            statement.execute(datesQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Calendar getCalendar(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM calendars WHERE id = " + id + ") JOIN (SELECT id as date_id, calendar_id as id, name as date_name, age as date_prio FROM dates) USING (id)";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Calendar calendar = new Calendar();
                calendar.setId(resultSet.getInt("id"));
                calendar.setName(resultSet.getString("name"));
                calendar.setYear(resultSet.getInt("year"));

                do {
                    Date date = new Date();
                    date.setId(resultSet.getInt("date_id"));
                    date.setName(resultSet.getString("date_name"));
                    date.setPrio(resultSet.getInt("date_prio"));
                    calendar.addDate(date);
                } while (resultSet.next());

                return calendar;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Date getDate(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM dates WHERE id=" + id;

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Date date = new Date();
                date.setId(resultSet.getInt("id"));
                date.setName(resultSet.getString("name"));
                date.setPrio(resultSet.getInt("prio"));
                return date;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Calendar> getCalendars() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM calendars) LEFT JOIN (SELECT id as date_id, calendar_id as id, name as date_name, age as date_prio FROM dates) USING (id)";

            List<Calendar> calendars = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Calendar calendar = null;

                do {
                    if (calendar == null || resultSet.getInt("id") != calendar.getId()) {
                        if (calendar != null) {
                            calendars.add(calendar);
                        }

                        calendar = new Calendar();
                        calendar.setId(resultSet.getInt("id"));
                        calendar.setName(resultSet.getString("name"));
                        calendar.setYear(resultSet.getInt("year"));
                    }

                    Date date = new Date();
                    date.setId(resultSet.getInt("date_id"));
                    if (resultSet.wasNull()) {
                        continue;
                    }
                    date.setName(resultSet.getString("date_name"));
                    date.setPrio(resultSet.getInt("date_prio"));
                    calendar.addDate(date);
                } while (resultSet.next());

                calendars.add(calendar);
            }

            return calendars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Date> getDates() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM dates";

            List<Date> dates = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Date date = new Date();
                date.setId(resultSet.getInt("id"));
                date.setName(resultSet.getString("name"));
                date.setPrio(resultSet.getInt("prio"));
                dates.add(date);
            }

            return dates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
