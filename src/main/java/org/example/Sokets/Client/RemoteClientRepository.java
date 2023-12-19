package org.example.Sokets.Client;


import org.example.Core.Models.Date;
import org.example.Core.Models.Calendar;
import org.example.Core.Repositories.Repository;
import org.example.Utils.Serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RemoteClientRepository implements Repository {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public RemoteClientRepository(String host, int port) throws IOException {
        socket = new Socket(host, port);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public int countCalendars() {
        try {
            out.writeInt(0);
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countDates() {
        try {
            out.writeInt(1);
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCalendar(Calendar calendar) {
        try {
            out.writeInt(2);
            String bytes = Serialization.toString(calendar);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateDate(Date date) {
        try {
            out.writeInt(3);
            String bytes = Serialization.toString(date);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveDatetoCalendar(int dateId, int calendarId) {
        try {
            out.writeInt(4);
            out.writeInt(dateId);
            out.writeInt(calendarId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertCalendar(Calendar calendar) {
        try {
            out.writeInt(5);
            String bytes = Serialization.toString(calendar);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertDate(int calendarId, Date date) {
        try {
            out.writeInt(6);
            out.writeInt(calendarId);
            String bytes = Serialization.toString(date);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCalendar(int id) {
        try {
            out.writeInt(7);
            out.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDate(int id) {
        try {
            out.writeInt(8);
            out.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Calendar getCalendar(int id) {
        try {
            out.writeInt(9);
            out.writeInt(id);

            int size = in.readInt();
            byte[] bytes = new byte[size];
            in.readFully(bytes);

            return (Calendar) Serialization.fromBytes(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getDate(int id) {
        try {
            out.writeInt(10);
            out.writeInt(id);

            int size = in.readInt();
            byte[] bytes = new byte[size];
            in.readFully(bytes);

            return (Date) Serialization.fromBytes(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Calendar> getCalendars() {
        try {
            out.writeInt(11);

            int count = in.readInt();
            List<Calendar> calendars = new ArrayList<>(count);

            for (int i = 0; i < count; ++i) {
                int size = in.readInt();
                byte[] bytes = new byte[size];
                in.readFully(bytes);
                calendars.add((Calendar) Serialization.fromBytes(bytes));
            }

            return calendars;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Date> getDates() {
        try {
            out.writeInt(12);

            int count = in.readInt();
            List<Date> dates = new ArrayList<>(count);

            for (int i = 0; i < count; ++i) {
                int size = in.readInt();
                byte[] bytes = new byte[size];
                in.readFully(bytes);
                dates.add((Date) Serialization.fromBytes(bytes));
            }

            return dates;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}