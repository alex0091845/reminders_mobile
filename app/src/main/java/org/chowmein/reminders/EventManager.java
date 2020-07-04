package org.chowmein.reminders;

/**
 * https://stackoverflow.com/questions/42553017/android-calculate-days-between-two-dates/48706121#:~:text=startDateValue%20%3D%20new%20Date(startDate)%3B,24)%20%2B%201%3B%20Log.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

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

        int id = 0;
        for(Event e : reminders) {
            final String CHANNEL_ID = e.toString();
            Notification reminder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.button)
                    .setContentTitle(e.getDesc())
                    .setContentText("In " + e.getDbr() + " days")
                    .build();
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(id, reminder);
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
