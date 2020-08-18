package org.chowmein.reminders.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.chowmein.reminders.managers.EventManager;

/**
 * A BroadcastReceiver that gets called on boot or at 12:00 AM (REGISTER_ALARM_ACTION)
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // remind on receiving onBoot broadcast or we're registering the alarm
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals(EventManager.REGISTER_ALARM_ACTION)) {
            EventManager.remind(context);
        }
    }
}
