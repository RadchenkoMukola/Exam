package org.example.Core.Repositories;

import org.example.Core.Models.Date;
import org.example.Core.Models.Calendar;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Repository extends Remote {
    int countCalendars() throws RemoteException;
    int countDates() throws RemoteException;

    void updateCalendar(Calendar calendar) throws RemoteException;
    void updateDate(Date date) throws RemoteException;

    void moveDatetoCalendar(int studentId, int groupId) throws RemoteException;

    void insertCalendar(Calendar calendar) throws RemoteException;

    void insertDate(int calendarId, Date date) throws RemoteException;

    void deleteCalendar(int id) throws RemoteException;
    void deleteDate(int id) throws RemoteException;

    Calendar getCalendar(int id) throws RemoteException;
    Date getDate(int id) throws RemoteException;

    List<Calendar> getCalendars() throws RemoteException;
    List<Date> getDates() throws RemoteException;
}
