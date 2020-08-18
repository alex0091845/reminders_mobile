package org.chowmein.reminders.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Event model to store the data input from the user.
 */
public class Event {
    private Date date;
    private String desc;
    private int dbr;
    private String year;         /* a convenience variable */
    private boolean isSelected;  /* indicates whether it's selected by the user */
    private boolean yearTop;     /* indicates whether it's the first event of the year or not */

    /**
     * Constructor that sets all data directly input by the user
     * @param date date of the event
     * @param desc description of the event
     * @param dbr starting day before the event that the user wants to reminded
     */
    public Event(Date date, String desc, int dbr) {
        this.date = date;
        this.desc = desc;
        this.dbr = dbr;

        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        this.year = yearFormat.format(date);
    }

    /**
     * Gets the date of the event.
     * Returns the date
     */
    public Date getDate() {
      return date;
    }

    /**
     * Gets the number of days before starting to remind the user (dbr).
     * Returns the dbr
     */
    public int getDbr() {
      return dbr;
    }

    /**
     * Gets the description of the event.
     * Returns the description of the event
     */
    public String getDesc() {
      return desc;
    }

    /**
     * Gets the year of the event.
     * Returns the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Returns whether the event view on the RecyclerView is selected.
     */
    public boolean isSelected() { return this.isSelected; }

    /**
     * Sets isSelected's state
     * @param selected the state
     */
    public void setSelected(boolean selected) { this.isSelected = selected; }

    /**
     * Returns whether this event is the first in its year.
     */
    public boolean isYearTop() {
        return this.yearTop;
    }

    /**
     * Sets whether this event is the first in its year.
     * @param yearTop true or false
     */
    public void setYearTop(boolean yearTop) {
        this.yearTop = yearTop;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Event)) {
            return false;
        }

        Event other = (Event) o;
        return date.equals(other.getDate()) &&
               desc.equals(other.getDesc()) &&
               dbr == other.getDbr();
    }

    @Override
    public String toString() {
        return "date: " + this.date +
               ", desc: " + this.desc +
               ", dbr: " + this.dbr;
    }
}