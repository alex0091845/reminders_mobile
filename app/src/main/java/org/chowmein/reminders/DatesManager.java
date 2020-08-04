package org.chowmein.reminders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatesManager {
    public static final int MONTH = 0;
    public static final int DAY_OF_MONTH = 1;
    public static final int YEAR = 2;

    public static final int ROUND_UP = 0;
    public static final int ROUND_DOWN = 1;

    // encapsulate constructor
    private DatesManager() {}

    public static String fromDateToString(Date date, int mode) {
        String result;

        switch (mode) {
            case MONTH:
                DateFormat monthFormat = new SimpleDateFormat("M");
                result = monthFormat.format(date);
            case DAY_OF_MONTH:
                DateFormat domFormat = new SimpleDateFormat("dd");
                result = domFormat.format(date);
            case YEAR:
                DateFormat yearFormat = new SimpleDateFormat("yyyy");
                result = yearFormat.format(date);
            default:
                result = date.toString();
        }

        return result;
    }

    /**
     * Converts a UTC Calendar time to local time in milliseconds
     * @param time
     * @return
     */
    public static long utcToLocalTime(Calendar time) {
        Date date = time.getTime();

        // gets the timezone offset
        int timeZoneOffset = time.getTimeZone().getOffset(date.getTime());

        // offsets the UTC time by the amount needed for local
        long localTime = date.getTime() + timeZoneOffset;

        return localTime;
    }

    public static long localToUTCTime(Calendar time) {
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
    public static long roundToDay(long time, int mode) {
        Date today = new Date(time);
        System.out.println("input: " + today);

        if(mode == ROUND_DOWN) {
            return time - (time % 86400000L);
        } else if (mode == ROUND_UP) {
            long result = time + (86400000L - (time % 86400000L));
            Date date = new Date(result);
            System.out.println("result: " + date);
            return result;
        }
        return -1;
    }

//    /**
//     * Calculates the difference between two times in milliseconds, namely a - b.
//     * @param a first time in milliseconds
//     * @param b second time in milliseconds
//     * @return the difference between a and b
//     */
//    public static long diffTime(long a, long b) {
//
//    }
}
