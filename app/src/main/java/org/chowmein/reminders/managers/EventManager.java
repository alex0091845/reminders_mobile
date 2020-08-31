package org.chowmein.reminders.managers;

/*
 * ------------------------------------------References---------------------------------------------
 * Calculate difference between days:
 * https://stackoverflow.com/questions/42553017/android-calculate-days-between-two-dates/48706121#:~:text=startDateValue%20%3D%20new%20Date(startDate)%3B,24)%20%2B%201%3B%20Log.
 * Notification:
 * https://stackoverflow.com/questions/45668079/notificationchannel-issue-in-android-o#:~:text=2%20Answers&text=If%20you%20target%20Android%20O,need%20to%20create%20a%20NotificationChannel.
 * Supporting different APIs/platforms:
 * https://developer.android.com/training/basics/supporting-devices/platforms
 * Removing notification channels:
 * https://stackoverflow.com/questions/51801940/how-to-remove-old-notification-channels
 *
 */

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.chowmein.reminders.receivers.AlertReceiver;
import org.chowmein.reminders.model.Event;
import org.chowmein.reminders.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A static class to manage events/reminders. It deploys the reminding operation and sets up an
 * "appointment" to perform the same operation for the next day. Each "appointment" will check
 * whether or not the user should be reminded of events and what those events are.
 */
public class EventManager {
    private final static int REGISTER_ALARM = 1;
    private final static double DAY_IN_MS = 86400000.0;
    private final static String CID = "remind";  // channel id

    public final static String REGISTER_ALARM_ACTION = "REGISTER_ALARM";
    private static final String NOTIFICATION_CHANNEL_NAME = "On boot";
    private static final String NOTIFICATION_CHANNEL_DESC = "Remind events";
    private static final int SMALL_ICON_RES = R.mipmap.ic_notif_small;

    private static ArrayList<Event> reminders;

    /**
     * Reminds the user of the events that are within days before reminding with respect to today.
     * @param context the current context
     */
    public static void remind(Context context) {
        if(reminders != null) {
            reminders.clear();
        }
        Preferences.loadPreferences(context);
        filterReminders(context);
        if(reminders == null) return;

        // sets up notification manager
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // sets up notification channel
        initNotificationChannel(notificationManager);

        // remind user of each event within the dbr of the current date
        notifyAll(context, notificationManager);

        registerAlarmTomorrow(context);
    }

    /**
     * Sets up notification channel (for supporting API > 26).
     * @param notificationManager the notification manager
     */
    private static void initNotificationChannel(NotificationManager notificationManager) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CID, NOTIFICATION_CHANNEL_NAME,
                    importance);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);

            // set notification sound
            AudioAttributes audAttr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audAttr);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Remind user of each event within the dbr of the current date
     * @param context the context
     * @param notificationManager the notification manager
     */
    private static void notifyAll(Context context, NotificationManager notificationManager) {
        // keep track of the ids for each of the notifications
        int id = 0;

        for(Event e : reminders) {
            NotificationCompat.Builder reminderBuilder = new NotificationCompat.Builder(context,
                    CID);

            // only sound the first notification
            if(id == 0) {
                reminderBuilder.setSound(Preferences.ringtoneUri);
            }

            int notifColor = context.getResources().getColor(R.color.maliblue);

            Notification reminder = reminderBuilder.setSmallIcon(SMALL_ICON_RES)
                    .setColor(notifColor)
                    .setContentTitle(e.getDesc())
                    .setContentText(getDaysAwayString(daysUntilEvent(e)))
                    .build();
            notificationManager.notify(id, reminder);
            id++;
        }
    }

    /**
     * A helper method to filter out a list of events that should be reminded today.
     * @param context the context
     */
    private static void filterReminders(Context context) {

        File saveFile = new File(context.getFilesDir().getPath(), JsonHelper.SAVE_FILE_NAME);
        ArrayList<Event> list = JsonHelper.deserialize(saveFile);

        if(list == null) return;

        if(reminders == null) { reminders = new ArrayList<>(); }

        // calculate whether the event is within days before reminding, and add to the reminding
        // list if so
        for(Event e : list) {
            long diffDays = daysUntilEvent(e);
            int dbr = e.getDbr();

            if(diffDays <= dbr && diffDays >= 0) {
                reminders.add(e);
            }
        }
    }

    /**
     * A helper method to register an "appointment" (alarm) for the following day (at 12:00am) so
     * the application and properly remind users then.
     * @param context the context
     */
    public static void registerAlarmTomorrow(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // create PendingIntent for the alarm to use
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction(REGISTER_ALARM_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REGISTER_ALARM,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // calculate the correct local representation of time for 12:00am
        Calendar utcCal = Calendar.getInstance();
        long localToday = DatesManager.utcToLocalTime(utcCal);

        long localTomorrow = DatesManager.roundToDay(localToday, DatesManager.ROUND_UP);
        Date localDate = new Date(localTomorrow);
        Calendar localCal = Calendar.getInstance();
        localCal.setTime(localDate);

        long tomorrow = DatesManager.localToUTCTime(localCal);

        alarmManager.set(AlarmManager.RTC, tomorrow, pendingIntent);
    }

    /**
     * A helper method to calculate the number of days until the event
     * @param e the event to calculate days until
     * @return the number of days until the event
     */
    private static int daysUntilEvent(Event e) {
        // get the local time
        Calendar cal = Calendar.getInstance();
        long localTime = DatesManager.utcToLocalTime(cal);

        // adjust the time to 00:00 for "today" with time zone considerations
        long adjustedTime = DatesManager.roundToDay(localTime, DatesManager.ROUND_DOWN);

        // calculate difference b/t days in milliseconds
        long diff = e.getDate().getTime() - adjustedTime;

        // round down the difference when dividing to see how many days in difference
        // so that it will accurately reflect the strict difference in days
        return (int) Math.floor(diff / DAY_IN_MS);   // difference in days
    }

    /**
     * A method to say how many days in prose: "In >1 days", "Tomorrow", or "Today."
     * @param days number of days until the event
     * @return the String prose
     */
    private static String getDaysAwayString(int days) {
        String daysAway;

        if(days > 1)        daysAway = "In " + days + " days";
        else if (days == 1) daysAway = "Tomorrow";
        else                daysAway = "Today";

        return daysAway;
    }
}
