package org.chowmein.reminders.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import org.chowmein.reminders.managers.UIFormatter;

public class GeneralPreference extends Preference {
    public GeneralPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GeneralPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GeneralPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneralPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        UIFormatter.formatGenPref(holder);
    }
}
