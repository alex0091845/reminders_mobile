package org.chowmein.reminders.managers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A helper class to operate on dates.
 */
public class DatesManager {
    public static final long DAY_IN_MS = 86400000L;
    public static final String DATE_PTRN = "M/d/yyyy";
    public static final String MONTH_DAY_PTRN = "M/d";
    public static final String MONTH_PTRN = "M";
    public static final String DAY_PTRN = "d";
    public static final String YEAR_PTRN = "yyyy";

    public static final int ROUND_UP = 0;
    public static final int ROUND_DOWN = 1;

    /** Encapsulate default constructor */
    private DatesManager() {}

    /**
     * Converts a UTC Calendar time to local time in milliseconds
     * @param time the time
     * Returns the local time in milliseconds
     */
    static long utcToLocalTime(Calendar time) {
        Date date = time.getTime();

        // gets the timezone offset
        int timeZoneOffset = time.getTimeZone().getOffset(date.getTime());

        // offsets the UTC time by the amount needed for local
        long localTime = date.getTime() + timeZoneOffset;

        return localTime;
    }

    /**
     * Converts a "local" Calendar time
     * @param time the local Calendar time
     * @return the UTC time
     */
    static long localToUTCTime(Calendar time) {
        Date date = time.getTime();

        // gets the timezone offset
        int timeZoneOffset = time.getTimeZone().getOffset(date.getTime());

        // offsets the UTC time by the amount needed for local
        long utcTime = date.getTime() - timeZoneOffset;

        return utcTime;
    }

    /**
     * Rounds time in milliseconds down to a day's start at 12:00 AM. (00:00 military)
     * @param time the time to round down to
     * @return day rounded down in milliseconds
     */
    static long roundToDay(long time, int mode) {
        if(mode == ROUND_DOWN) {
            return time - (time % DAY_IN_MS);
        } else if (mode == ROUND_UP) {
            long result = time + (DAY_IN_MS - (time % DAY_IN_MS));
            return result;
        }
        return -1;
    }

    /**
     * Used to format a date into a certain pattern
     * @param date the date to format
     * @param pattern the format/pattern to format the date into
     * @return the formatted date as a String
     */
    public static String formatDate(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * Used to parse a date string into a Date
     * @param date the date string
     * @param pattern the pattern of the date
     * @return a Date object
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.parse(date);
    }

    /**
     * Returns a formatted date string given a date, how many days to subtract from it,
     * and formats it into the pattern
     * @param time the given date
     * @param days days to subtract
     * @param pattern pattern format
     * @return the resulting formatted date string
     */
    public static String getDateStringFromDifference(long time, int days, String pattern) {
        long offset = (long)(days * DAY_IN_MS);
        return formatDate(new Date(time - offset), pattern);
    }

    public static String getCurrYearString() {
        return new SimpleDateFormat(YEAR_PTRN).format(new Date());
    }
}
