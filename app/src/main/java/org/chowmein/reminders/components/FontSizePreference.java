package org.chowmein.reminders.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

import org.chowmein.reminders.R;
import org.chowmein.reminders.managers.DatesManager;
import org.chowmein.reminders.managers.Preferences;
import org.chowmein.reminders.managers.UIFormatter;

public class FontSizePreference extends SeekBarPreference {

    public FontSizePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FontSizePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FontSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FontSizePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        View eventView = view.findViewById(R.id.list_item);
        UIFormatter.formatEventItem(eventView, Preferences.getFontSize());
        UIFormatter.formatFontSizePref(view);

        // set tv_event_year
        TextView tv_event_year = eventView.findViewById(R.id.tv_event_year);
        tv_event_year.setVisibility(View.VISIBLE);
        tv_event_year.setText(DatesManager.getCurrYearString());

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        sharedPrefs.registerOnSharedPreferenceChangeListener(
                // TODO: considering to save a list of what changed
                (preference, key) -> {
                    // if any prefs have changed, set prefsChanged to true
                    Preferences.prefsChanged = true;

                    // if the font size changed, update list item preview
                    if(key.equals(Preferences.FONT_SIZE_KEY)) {
                        UIFormatter.formatEventItem(
                                eventView,
                                preference.getInt(key, Preferences.getFontSize())
                        );
                    }

                }
        );
    }
}
