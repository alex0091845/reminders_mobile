package org.chowmein.reminders;

/**
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
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.Preference;
import androidx.recyclerview.widget.SortedList;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventManager {
    private final static int REGISTER_ALARM = 1;

    private static ArrayList<Event> reminders;

    public static void remind(Context context) {
        if(reminders != null) {
            reminders.clear();
        }
        Preferences.loadPreferences(context);
        filterReminders(context);
        if(reminders == null) return;

        // sets up notification manager
        NotificationManager notifMngr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // channel ID
        String cId = "remind";

        // sets up notification channel
        // support for Oreo and above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "On boot";
            String description = "Remind events";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(cId, name, importance);
            channel.setDescription(description);

            // set notification sound
            AudioAttributes audAttr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audAttr);

            notifMngr.createNotificationChannel(channel);
        }

        // keep track of the ids for each of the notifications
        int id = 0;

        // remind user of each event within the dbr of the current date
        for(Event e : reminders) {
            System.out.println(Preferences.ringtoneUri);
            NotificationCompat.Builder reminderBuilder = new NotificationCompat.Builder(context, cId);

            // set only the first notification to sound
            if(id == 0) {
                reminderBuilder.setSound(Preferences.ringtoneUri);
            }

            Notification reminder = reminderBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(e.getDesc())
                    .setContentText(getDaysAwayString(diffDays(e)))
                    .build();
            notifMngr.notify(id, reminder);
            id++;
        }

        registerAlarmTomorrow(context);
    }

    private static void filterReminders(Context context) {

        File saveFile = new File(context.getFilesDir().getPath(), "saveFile.json");
        ArrayList<Event> list = JsonHelper.deserialize(saveFile);

        if(list == null) return;

        if(reminders == null) reminders = new ArrayList<>();

        for(Event e : list) {
            long diffDays = diffDays(e);
            int dbr = e.getDbr();

            if(diffDays <= dbr && diffDays >= 0) {
                reminders.add(e);
            }
        }
    }

    private static void registerAlarmTomorrow(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, BootDeviceReceiver.class);
        intent.setAction("REGISTER_ALARM");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REGISTER_ALARM,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar utcCal = Calendar.getInstance();
        long localToday = DatesManager.utcToLocalTime(utcCal);

        long localTomorrow = DatesManager.roundToDay(localToday, DatesManager.ROUND_UP);
        Date localDate = new Date(localTomorrow);
        Calendar localCal = Calendar.getInstance();
        localCal.setTime(localDate);

        long tomorrow = DatesManager.localToUTCTime(localCal);

        alarmManager.set(AlarmManager.RTC, tomorrow, pendingIntent);
    }

    private static long diffDays(Event e) {
        // get the local time
        Calendar cal = Calendar.getInstance();
        long localTime = DatesManager.utcToLocalTime(cal);

        // adjust the time to 00:00 for "today" with time zone considerations
        long adjustedTime = DatesManager.roundToDay(localTime, DatesManager.ROUND_DOWN);

        // calculate difference b/t days in milliseconds
        long diff = e.getDate().getTime() - adjustedTime;

        // round down the difference when dividing to see how many days in difference
        // so that it will accurately reflect the strict difference in days
        long diffDays = (long) Math.floor(diff / 86400000.0);

        return diffDays;
    }

    private static String getDaysAwayString(long days) {
        String daysAway;

        if(days > 1)        daysAway = "In " + days + " days";
        else if (days == 1) daysAway = "Tomorrow";
        else                daysAway = "Today";

        return daysAway;
    }
}
