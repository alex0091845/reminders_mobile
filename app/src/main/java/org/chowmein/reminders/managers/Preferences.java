package org.chowmein.reminders.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import org.chowmein.reminders.helpers.ThemeHelper;

/**
 * A static class used to abstract away the minute details of getting the preferences
 * and instead just assigns each preference to its own public static variable for other
 * classes to access globally. The method loadPreferences(Context) must be called first.
 */
public class Preferences {
    public static final String FONT_SIZE_KEY = "fontSize";
    public static final String RINGTONE_KEY = "ringtone";
    public static final String THEME_KEY = "theme";
    public static final String DEFAULT_RINGTONE_VALUE = "None";
    public static final int DEFAULT_FONT_SIZE = 22;
    public static final String DEFAULT_THEME = "Red Velvet";

    /* indicating whether any prefs have changed. This will be set in the onCreate() callback
    * of the SettingsActivity, and set again to true in the onPreferenceChanged() in its inner
    * class, SettingsFragment. */
    public static boolean prefsChanged;

    private static int fontSize;
    static Uri ringtoneUri;

    private static String theme = ThemeHelper.OCEAN;

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
        SharedPreferences shrdprefs = PreferenceManager.getDefaultSharedPreferences(context);

        Preferences.setFontSize(shrdprefs.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE));
        Preferences.ringtoneUri = Uri.parse(shrdprefs.getString(RINGTONE_KEY,
                DEFAULT_RINGTONE_VALUE));
        Preferences.theme = shrdprefs.getString(THEME_KEY, DEFAULT_THEME);
    }

    /**
     * Gets the ringtone name from a Uri to display to the user.
     * @param context the context
     * @param ringtoneUri the ringtone uri
     * @return title/name of the ringtone
     */
    public static String getRingtoneName(Context context, Uri ringtoneUri) {
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        return ringtone.getTitle(context);
    }

    public static String getCurrentRingtoneName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(RINGTONE_KEY, DEFAULT_RINGTONE_VALUE);
    }

    /**
     * Getter for fontSize
     * @return static fontSize
     */
    public static int getFontSize() {
        return fontSize;
    }

    /**
     * Setter for fontSize
     * @param fontSize input fontSize
     */
    public static void setFontSize(int fontSize) {
        Preferences.fontSize = fontSize;
    }


    /**
     * Gets theme string
     * @return theme
     */
    public static String getTheme() {
        return theme;
    }


    public static void setTheme(String theme) {
        Preferences.theme = theme;
    }
}
