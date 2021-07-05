package org.chowmein.reminders.managers;

/*
 * ----------------------------------------References-----------------------------------------------
 * Get screen width and height pixels:
 * https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
 * Setting constraint layout margins:
 * https://stackoverflow.com/questions/52148129/programmatically-set-margin-to-constraintlayout
 */

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceViewHolder;

import org.chowmein.reminders.R;
import org.chowmein.reminders.activities.HomeActivity;

/**
 * A helper class to format the UI components of Activities.
 */
public class UIFormatter {

    public final static int HOME = 0;
    public final static int ADDEDIT = 1;
    public final static int SETTINGS = 2;

    private final static int LARGE_OFFSET = 2; // (20sp)
    public final static int MEDIUM_OFFSET = 4; // (18sp)
    public final static int SMALL_OFFSET = 6;  // (16sp)
    public final static int SMOL_OFFSET = 8;   // (14sp)
    private final static int SIDE_MARGINS = 20;
    private final static int LEFT_MARGIN = 10;

    private static int width;

    /**
     * Formats the Activity screen UI based on the activityId, as pulled from the constants above.
     * @param activity the activity
     * @param activityId the activity's id
     */
    public static void format(Activity activity, int activityId) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        switch (activityId) {
            case HOME:
                formatHome(activity);
                break;
            case ADDEDIT:
                formatAddEdit(activity);
                break;
            case SETTINGS:
                formatSettings(activity);
                break;
        }
    }

    public static void formatButton(View v) {
        ((Button)v).setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);
    }

    public static void formatTVSmol(View v) {
        ((TextView)v).setTextSize(Preferences.getFontSize() - SMOL_OFFSET);
    }


    public static void formatTVSmall(View v) {
        ((TextView)v).setTextSize(Preferences.getFontSize() - SMALL_OFFSET);
    }

    public static void formatTVMedium(View v) {
        ((TextView)v).setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);
    }

    public static void formatTVLarge(View v) {
        ((TextView)v).setTextSize(Preferences.getFontSize() - LARGE_OFFSET);
    }

    private static void formatSettings(Activity activity) {
        formatButton(activity.findViewById(R.id.btn_cancel_settings));
        formatButton(activity.findViewById(R.id.btn_apply_settings));
    }

    public static void formatGenPref(PreferenceViewHolder view) {
        formatTVMedium(view.findViewById(android.R.id.title));
        formatTVSmol(view.findViewById(android.R.id.summary));
    }

    public static void formatFontSizePref(PreferenceViewHolder view) {
        formatGenPref(view);
        formatTVSmall(view.findViewById(R.id.tv_preview));
    }

    /**
     * A helper method to format the add/edit Activity, which are awfully similar.
     * @param activity supposedly the add/edit Activity
     */
    private static void formatAddEdit(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date);
        setMarginAndWidthAddEdit(tvDate);
        tvDate.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);

        Button btnDate = activity.findViewById(R.id.btn_date);
        setMarginAndWidthAddEdit(btnDate);
        btnDate.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        TextView tvDesc = activity.findViewById(R.id.tv_desc);
        setMarginAndWidthAddEdit(tvDesc);
        tvDesc.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);

        EditText edtDesc = activity.findViewById(R.id.edt_desc);
        setMarginAndWidthAddEdit(edtDesc);
        edtDesc.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        TextView tvDbr = activity.findViewById(R.id.tv_dbr);
        setMarginAndWidthAddEdit(tvDbr);
        tvDbr.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);

        EditText edtDbr = activity.findViewById(R.id.edt_dbr);
        setMarginAndWidthAddEdit(edtDbr);
        edtDbr.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        Button btnSubmit = activity.findViewById(R.id.btn_submit);
        btnSubmit.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        Button btnCancel = activity.findViewById(R.id.btn_cancel);
        btnCancel.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);
    }

    /**
     * A helper method to set each of the UI components on add/edit form
     * @param v each of the views
     */
    private static void setMarginAndWidthAddEdit(View v) {
        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) v.getLayoutParams();
        cl.leftMargin = width / SIDE_MARGINS;
        cl.width = width - (width / LEFT_MARGIN);
    }

    /**
     * A helper method to format HomeActivity UI
     * @param activity supposedly the HomeActivity
     */
    private static void formatHome(Activity activity) {
        HomeActivity homeActivity = (HomeActivity) activity;
        TextView tvHomeYear = homeActivity.findViewById(R.id.tv_home_year);
        tvHomeYear.setTextSize(Preferences.getFontSize() - UIFormatter.MEDIUM_OFFSET);
    }

    public static void formatEventItem(View eventView, int prefFontSize) {
        TextView tv_desc = eventView.findViewById(R.id.tv_event_desc);
        tv_desc.setTextSize(prefFontSize);

        TextView tv_date = eventView.findViewById(R.id.tv_event_date);
        tv_date.setTextSize(prefFontSize);

        TextView tv_dbr = eventView.findViewById(R.id.tv_event_dbr);
        tv_dbr.setTextSize(prefFontSize - UIFormatter.SMALL_OFFSET);

        TextView tv_year = eventView.findViewById(R.id.tv_event_year);
        tv_year.setTextSize(prefFontSize - UIFormatter.MEDIUM_OFFSET);
    }
}
