package org.chowmein.reminders.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import org.chowmein.reminders.managers.UIFormatter;

public class GeneralPreferenceCategory extends PreferenceCategory {
    public GeneralPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GeneralPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GeneralPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneralPreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView tv_title = (TextView) holder.findViewById(android.R.id.title);
        UIFormatter.formatTVMedium(tv_title);
        UIFormatter.formatTextViewToTheme(tv_title, this.getContext());
    }


}
