package org.chowmein.reminders;

/**
 * Calculate difference between days
 * https://stackoverflow.com/questions/42553017/android-calculate-days-between-two-dates/48706121#:~:text=startDateValue%20%3D%20new%20Date(startDate)%3B,24)%20%2B%201%3B%20Log.
 * Notification:
 * https://stackoverflow.com/questions/45668079/notificationchannel-issue-in-android-o#:~:text=2%20Answers&text=If%20you%20target%20Android%20O,need%20to%20create%20a%20NotificationChannel.
 * Supporting different APIs/platforms
 * https://developer.android.com/training/basics/supporting-devices/platforms
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class EventManager {
    private static ArrayList<Event> reminders;

    public static void remind(Context context) {
        filterReminders(context);
        if(reminders == null) return;

        // sets up notification manager
        NotificationManager notifMngr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // channel ID
        String cId = "boot";

        // sets up notification channel
        // support for Oreo and above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "On boot";
            String description = "Remind events on boot";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(cId, name, importance);
            channel.setDescription(description);

            notifMngr.createNotificationChannel(channel);
        }

        // keep track of the ids for each of the notifications
        int id = 0;

        // remind user of each event within the dbr of the current date
        for(Event e : reminders) {
            Notification reminder = new NotificationCompat.Builder(context, cId)
                    .setSmallIcon(R.drawable.button)
                    .setContentTitle(e.getDesc())
                    .setContentText("In " + e.getDbr() + " days")
                    .build();
            notifMngr.notify(id, reminder);
            id++;
        }
    }

    private static void filterReminders(Context context) {
        Date today = new Date();
        File saveFile = new File(context.getFilesDir().getPath(), "saveFile.json");
        ArrayList<Event> list = JsonHelper.deserialize(saveFile);

        if(list == null) return;

        if(reminders == null) reminders = new ArrayList<>();

        for(Event e : list) {
            Date eventDate = e.getDate();
            int dbr = e.getDbr();

            // calculate difference b/t days
            long diff = eventDate.getTime() - today.getTime();

            // add to list of reminders if the user should be reminded of the event
            long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if(diffDays <= dbr) {
                reminders.add(e);
            }
        }
    }
}
