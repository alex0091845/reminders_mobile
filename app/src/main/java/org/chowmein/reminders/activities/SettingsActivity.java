package org.chowmein.reminders.activities;

/*
 * -----------------------------References
 * Get ringtone info:
 * https://stackoverflow.com/questions/7671637/how-to-set-ringtone-with-ringtonemanager-action-ringtone-picker
 *
 * Open notification channel settings:
 * https://developer.android.com/training/notify-user/channels#UpdateChannel
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.chowmein.reminders.R;
import org.chowmein.reminders.helpers.ThemeHelper;
import org.chowmein.reminders.managers.EventManager;
import org.chowmein.reminders.managers.Preferences;
import org.chowmein.reminders.managers.UIFormatter;


/**
 * The Activity for when the user needs to adjust Settings.
 */
public class SettingsActivity
        extends AppCompatActivity {

    private int initFontSize;
    private String initRingtoneName;
    private static final int O_RINGTONE_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getThemeStyle());
        super.onCreate(savedInstanceState);

        // indicated that, for now, preferences have not been changed
        Preferences.prefsChanged = false;
        this.initFontSize = Preferences.getFontSize();
        this.initRingtoneName = Preferences.getCurrentRingtoneName(this);

        setContentView(R.layout.settings_activity);
        initViews();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        Button btnCancel = findViewById(R.id.btn_cancel_settings);
        btnCancel.setOnClickListener(e -> onBackButtonPressed());

        Button btnApply = findViewById(R.id.btn_apply_settings);
        btnApply.setOnClickListener(e -> onApplyButtonPressed());
    }

    private void onBackButtonPressed() {
        Preferences.prefsChanged = false;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(Preferences.FONT_SIZE_KEY, initFontSize)
                .putString(Preferences.RINGTONE_KEY, initRingtoneName)
                .apply();
        finish();
    }

    private void onApplyButtonPressed() {
        finish();
    }

    /**
     * The inner class (generated) fragment. It loads all the initial values of the settings, and
     * implements how the user will interact with each setting.
     */
    public static class SettingsFragment
            extends PreferenceFragmentCompat{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // format the settings activity in general
            UIFormatter.format(getActivity(), UIFormatter.SETTINGS);

            // go through all this just to get the name of the ringtone
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getContext());
            String ringtoneStr = sharedPrefs.getString(Preferences.RINGTONE_KEY,
                    Preferences.DEFAULT_RINGTONE_VALUE);

            // default uri to null, then set if not Silent
            Uri ringtoneUri = Uri.parse(ringtoneStr);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ringtoneUri = getUriVersionOAndAbove();
            }

            // default name to null, then set if not Silent
            String ringtoneName = "None";
            if(ringtoneUri == null || !ringtoneUri.toString().equals("None")) {
                ringtoneName = Preferences.getRingtoneName(this.getContext(), ringtoneUri);
            }

            // set up the ringtonePicker intent and onClick listener
            Preference ringtonePref = findPreference(Preferences.RINGTONE_KEY);
            ringtonePref.setSummary(ringtoneName);

            ringtonePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent notificationSettings =
                                new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                        notificationSettings.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                        notificationSettings.putExtra(Settings.EXTRA_CHANNEL_ID, EventManager.CID);
                        startActivityForResult(notificationSettings, O_RINGTONE_REQ_CODE);
                        return true;
                    }

                    // go through all this just to get the name of the ringtone
                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(SettingsFragment.this.getContext());
                    String ringtoneStr = sharedPrefs.getString(Preferences.RINGTONE_KEY,
                            Preferences.DEFAULT_RINGTONE_VALUE);

                    // default uri to null, then set if not Silent
                    Uri ringtoneUri = Uri.parse(ringtoneStr);

                    Intent ringtonePicker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                            RingtoneManager.TYPE_NOTIFICATION);

                    if(!ringtoneUri.toString().equals(Preferences.DEFAULT_RINGTONE_VALUE)) {
                        ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                ringtoneUri);
                    }

                    startActivityForResult(ringtonePicker, RingtoneManager.TYPE_NOTIFICATION);
                    return true;
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private Uri getUriVersionOAndAbove() {
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel nc = notificationManager.getNotificationChannel(EventManager.CID);
            return nc.getSound();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(data == null && requestCode != O_RINGTONE_REQ_CODE) return;

            Uri ringtoneUri;

            /* Android version above O */
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ringtoneUri = getUriVersionOAndAbove();
            } else {
                /* Android version below O */
                ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            }

            SharedPreferences sharedPrefs = PreferenceManager.
                    getDefaultSharedPreferences(this.getContext());
            
            // set default to None
            String ringtoneUriStr = Preferences.DEFAULT_RINGTONE_VALUE;
            String ringtoneName = Preferences.DEFAULT_RINGTONE_VALUE;

            if(ringtoneUri != null
                    && !ringtoneUri.toString().equals(Preferences.DEFAULT_RINGTONE_VALUE)) {
                ringtoneUriStr = ringtoneUri.toString();
                ringtoneName = Preferences.getRingtoneName(this.getContext(), ringtoneUri);
            }

            // store the uri into the ringtone preference
            sharedPrefs.edit().putString(Preferences.RINGTONE_KEY, ringtoneUriStr).apply();

            // set the summary of the pref to the ringtone name
            findPreference(Preferences.RINGTONE_KEY).setSummary(ringtoneName);
        }

    }
}