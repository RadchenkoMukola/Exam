package org.example.Sokets.Server;


import org.example.Core.Models.Date;
import org.example.Core.Models.Calendar;
import org.example.Core.Repositories.Repository;
import org.example.Utils.Serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

public class RemoteServerRepository implements Repository {
    private final ServerSocket serverSocket;

    private final DataInputStream in;
    private final DataOutputStream out;

    private final Repository repository;

    public RemoteServerRepository(Repository repository, int port) {
        try {
            serverSocket = new ServerSocket(8080);

            Socket socket = serverSocket.accept();

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.repository = repository;
    }

    @Override
    public int countCalendars() throws RemoteException {
        return repository.countCalendars();
    }

    @Override
    public int countDates() throws RemoteException {
        return repository.countDates();
    }

    @Override
    public void updateCalendar(Calendar calendar) throws RemoteException {
        repository.updateCalendar(calendar);
    }

    @Override
    public void updateDate(Date date) throws RemoteException {
        repository.updateDate(date);
    }

    @Override
    public void moveDatetoCalendar(int dateId, int calendarId) throws RemoteException {
        repository.moveDatetoCalendar(dateId, calendarId);
    }

    @Override
    public void insertCalendar(Calendar calendar) throws RemoteException {
        repository.insertCalendar(calendar);
    }

    @Override
    public void insertDate(int calendarId, Date date) throws RemoteException {
        repository.insertDate(calendarId, date);
    }

    @Override
    public void deleteCalendar(int id) throws RemoteException {
        repository.deleteCalendar(id);
    }

    @Override
    public void deleteDate(int id) throws RemoteException {
        repository.deleteDate(id);
    }

    @Override
    public Calendar getCalendar(int id) throws RemoteException {
        return repository.getCalendar(id);
    }

    @Override
    public Date getDate(int id) throws RemoteException {
        return repository.getDate(id);
    }

    @Override
    public List<Calendar> getCalendars() throws RemoteException {
        return repository.getCalendars();
    }

    @Override
    public List<Date> getDates() throws RemoteException {
        return repository.getDates();
    }

    void start() {
        try {
            while (true) {
                int operation = in.readInt();
                System.out.println("Received operation: " + operation);

                boolean isOk = switch (operation) {
                    case 0 -> {
                        out.writeInt(repository.countCalendars());

                        yield true;
                    }
                    case 1 -> {
                        out.writeInt(repository.countDates());

                        yield true;
                    }
                    case 2 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.updateCalendar((Calendar) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 3 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.updateDate((Date) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 4 -> {
                        int dateId = in.readInt();
                        int calendarId = in.readInt();

                        repository.moveDatetoCalendar(dateId, calendarId);

                        yield true;
                    }
                    case 5 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.insertCalendar((Calendar) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 6 -> {
                        int calendarId = in.readInt();

                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.insertDate(calendarId, (Date) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 7 -> {
                        int id = in.readInt();

                        repository.deleteCalendar(id);

                        yield true;
                    }
                    case 8 -> {
                        int id = in.readInt();

                        repository.deleteDate(id);

                        yield true;
                    }
                    case 9 -> {
                        int id = in.readInt();

                        Calendar calendar = repository.getCalendar(id);
                        String bytes = Serialization.toString(calendar);
                        out.writeInt(bytes.length());
                        out.writeBytes(bytes);

                        yield true;
                    }
                    case 10 -> {
                        int id = in.readInt();

                        Date date = repository.getDate(id);
                        String bytes = Serialization.toString(date);
                        out.writeInt(bytes.length());
                        out.writeBytes(bytes);

                        yield true;
                    }
                    case 11 -> {
                        List<Calendar> calendars = repository.getCalendars();

                        out.writeInt(calendars.size());

                        for (Calendar calendar: calendars) {
                            String bytes = Serialization.toString(calendar);
                            out.writeInt(bytes.length());
                            out.writeBytes(bytes);
                        }

                        yield true;
                    }
                    case 12 -> {
                        List<Date> dates = repository.getDates();

                        out.writeInt(dates.size());

                        for (Date date: dates) {
                            String bytes = Serialization.toString(date);
                            out.writeInt(bytes.length());
                            out.writeBytes(bytes);
                        }

                        yield true;
                    }
                    default -> {
                        System.out.println("Unsupported operation");
                        yield false;
                    }
                };

                if (!isOk) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
