package org.chowmein.reminders.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

import org.chowmein.reminders.R;
import org.chowmein.reminders.managers.DatesManager;
import org.chowmein.reminders.managers.Preferences;
import org.chowmein.reminders.managers.UIFormatter;

public class FontSizePreference extends SeekBarPreference {
    private PreferenceViewHolder viewHolder;

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
        this.viewHolder = view;

        View eventView = view.findViewById(R.id.list_item);
        UIFormatter.formatFontSizePref(view, this.getContext());

        // set tv_event_year
        TextView tv_event_year = eventView.findViewById(R.id.tv_event_year);
        tv_event_year.setVisibility(View.VISIBLE);
        tv_event_year.setText(DatesManager.getCurrYearString());

        this.setOnPreferenceChangeListener((preference, newValue) -> {
            // TODO: considering to save a list of what changed
            Preferences.prefsChanged = true;

            Preferences.setFontSize((int) newValue);
            UIFormatter.format((Activity) this.getContext(), UIFormatter.SETTINGS);
            return true;
        });
    }

    public PreferenceViewHolder getViewHolder() {
        return this.viewHolder;
    }
}
