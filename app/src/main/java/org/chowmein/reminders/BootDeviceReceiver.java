package org.chowmein.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootDeviceReceiver extends BroadcastReceiver {

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            EventManager.remind(context);
        }
    }
}
