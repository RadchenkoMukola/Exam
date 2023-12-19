package org.example.RMI.Client;

import org.example.Core.Models.Calendar;
import org.example.Core.Models.Date;
import org.example.Core.Repositories.Repository;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(4097);
            Repository repository = (Repository) registry.lookup("repository");

            System.out.println("Calendars: " + repository.countCalendars());
            System.out.println("Dates: " + repository.countDates());

            Calendar calendar = new Calendar();
            calendar.setName("New Calendar");
            calendar.setYear(2020);

            repository.insertCalendar(calendar);

            Date date = new Date();
            date.setName("New Date");
            repository.insertDate(2, date);

            repository.deleteCalendar(1);
            repository.deleteDate(5);

            System.out.println("Calendar: " + repository.getCalendar(1));
            System.out.println("Date: " + repository.getDate(5));

            System.out.println("Calendars: " + repository.getCalendars());
            System.out.println("Dates: " + repository.getDates());
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
