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

import org.chowmein.reminders.R;
import org.chowmein.reminders.activities.HomeActivity;

/**
 * A helper class to format the UI components of Activities.
 */
public class UIFormatter {

    public final static int HOME = 0;
    public final static int ADDEDIT = 1;

    private final static int LARGE_OFFSET = 2;
    public final static int MEDIUM_OFFSET = 4;
    public final static int SMALL_OFFSET = 8;
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

        if(activityId == HOME) formatHome(activity);
        else if(activityId == ADDEDIT) formatAddEdit(activity);
    }

    /**
     * A helper method to format the add/edit Activity, which are awfully similar.
     * @param activity supposedly the add/edit Activity
     */
    private static void formatAddEdit(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date);
        setMarginAndWidthAddEdit(tvDate);
        tvDate.setTextSize(Preferences.fontSize - LARGE_OFFSET);

        Button btnDate = activity.findViewById(R.id.btn_date);
        setMarginAndWidthAddEdit(btnDate);
        btnDate.setTextSize(Preferences.fontSize - MEDIUM_OFFSET);

        TextView tvDesc = activity.findViewById(R.id.tv_desc);
        setMarginAndWidthAddEdit(tvDesc);
        tvDesc.setTextSize(Preferences.fontSize - LARGE_OFFSET);

        EditText edtDesc = activity.findViewById(R.id.edt_desc);
        setMarginAndWidthAddEdit(edtDesc);
        edtDesc.setTextSize(Preferences.fontSize - MEDIUM_OFFSET);

        TextView tvDbr = activity.findViewById(R.id.tv_dbr);
        setMarginAndWidthAddEdit(tvDbr);
        tvDbr.setTextSize(Preferences.fontSize - LARGE_OFFSET);

        EditText edtDbr = activity.findViewById(R.id.edt_dbr);
        setMarginAndWidthAddEdit(edtDbr);
        edtDbr.setTextSize(Preferences.fontSize - MEDIUM_OFFSET);

        Button btnSubmit = activity.findViewById(R.id.btn_submit);
        btnSubmit.setTextSize(Preferences.fontSize - MEDIUM_OFFSET);
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
        tvHomeYear.setTextSize(Preferences.fontSize - UIFormatter.MEDIUM_OFFSET);
    }
}
