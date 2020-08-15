package org.chowmein.reminders.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.chowmein.reminders.helpers.EventManager;

public class BootDeviceReceiver extends BroadcastReceiver {

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals("REGISTER_ALARM")) {
            EventManager.remind(context);
        }
    }
}
