package org.example.Sokets.Client;

import org.example.Core.Models.Calendar;
import org.example.Core.Models.Date;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        try {
            RemoteClientRepository repository = new RemoteClientRepository("localhost", 8080);

            System.out.println("Teams: " + repository.countCalendars());
            System.out.println("Players: " + repository.countDates());

            Calendar team = new Calendar();
            team.setName("New team");
            team.setYear(20);

            //repository.insertTeam(team);

            Date player = new Date();
            player.setName("New Player");
            //repository.insertPlayer(2, player);

            //repository.deleteTeam(1);
            //repository.deletePlayer(3);

            System.out.println("Calendar: " + repository.getCalendar(2));
            System.out.println("Date: " + repository.getDate(4));

            System.out.println("Calendars: " + repository.getCalendars());
            System.out.println("Dates: " + repository.getDates());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
