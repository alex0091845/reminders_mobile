package org.chowmein.reminders.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;

import org.chowmein.reminders.managers.UIFormatter;

public class ThemePreference extends ListPreference {
    PreferenceViewHolder viewHolder;

    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        this.viewHolder = holder;

        UIFormatter.formatGenPref(holder);
    }

    public PreferenceViewHolder getViewHolder() {
        return this.viewHolder;
    }
}
