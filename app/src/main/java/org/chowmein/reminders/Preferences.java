package org.chowmein.reminders;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * A static class used to abstract away the minute details of getting the preferences
 * and instead just assigns each preference to its own public static variable for other
 * classes to access globally. The method loadPreferences(Context) must be called first.
 */
class Preferences {

    static Context context;

    /* indicating whether any prefs have changed. This will be set in the onCreate() callback
    * of the SettingsActivity, and set again to true in the onPreferenceChanged() in its inner
    * class, SettingsFragment. */
    static boolean prefsChanged;

    public static int fontSize;

    /**
     * Hide the default constructor.
     */
    Preferences() {}

    /**
     * The "constructor" method that should be called in the onCreate callback in the main
     * activity. This will allow the classes like UIFormatter to retrieve the preferences
     * afterwards.
     * @param context main activity's context
     */
    public static void loadPreferences(Context context) {
        Preferences.context = context;
        SharedPreferences shrdprefs = PreferenceManager.getDefaultSharedPreferences(context);
        Preferences.fontSize = shrdprefs.getInt("fontSize", 22);
    }


}
