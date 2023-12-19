package org.example.Core.Models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Calendar implements Serializable {
    public static final String CALENDARS = "Calendars";
    public static final String CALENDAR = "Calendar";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String YEAR = "year";

    private int id;
    private String name;
    private int year;
    private List<Date> dates = new ArrayList<>();

    public boolean hasDate(Date date) {
        return dates.contains(date);
    }

    public void addDate(Date player) {
        dates.add(player);
    }

    public void removeDate(Date player) {
        dates.remove(player);
    }

    public Date getDateByIndex(int index) {
        return dates.get(index);
    }

    public Date getPlayerById(int id) {
        Date result = null;
        for (Date date : dates) {
            result = date;
        }
        return result;
    }

    public Date getLastDate() {
        return dates.isEmpty() ? null : dates.get(dates.size() - 1);
    }

    public boolean hasDates() {
        return !dates.isEmpty();
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", players=" + dates +
                '}';
    }
}
