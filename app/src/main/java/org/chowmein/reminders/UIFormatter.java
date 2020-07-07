package org.chowmein.reminders;

/**
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
import androidx.preference.Preference;

class UIFormatter {

    final static int HOME = 0;
    final static int ADDEDIT = 1;

    final static int TEXTVIEW_OFFSET = 2;
    final static int INPUT_OFFSET = 4;

    static int width;
    static int height;

    static void format(Activity activity, int activityId) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        if(activityId == HOME) formatHome(activity);
        else if(activityId == ADDEDIT) formatAddEdit(activity);
    }

    private static void formatAddEdit(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date);
        setMarginAndWidthAddEdit(tvDate);
        tvDate.setTextSize(Preferences.fontSize - TEXTVIEW_OFFSET);

        Button btnDate = activity.findViewById(R.id.btn_date);
        setMarginAndWidthAddEdit(btnDate);
        btnDate.setTextSize(Preferences.fontSize - INPUT_OFFSET);

        TextView tvDesc = activity.findViewById(R.id.tv_desc);
        setMarginAndWidthAddEdit(tvDesc);
        tvDesc.setTextSize(Preferences.fontSize - TEXTVIEW_OFFSET);

        EditText edtDesc = activity.findViewById(R.id.edt_desc);
        setMarginAndWidthAddEdit(edtDesc);
        edtDesc.setTextSize(Preferences.fontSize - INPUT_OFFSET);

        TextView tvDbr = activity.findViewById(R.id.tv_dbr);
        setMarginAndWidthAddEdit(tvDbr);
        tvDbr.setTextSize(Preferences.fontSize - TEXTVIEW_OFFSET);

        EditText edtDbr = activity.findViewById(R.id.edt_dbr);
        setMarginAndWidthAddEdit(edtDbr);
        edtDbr.setTextSize(Preferences.fontSize - INPUT_OFFSET);

        Button btnSubmit = activity.findViewById(R.id.btn_submit);
        btnSubmit.setTextSize(Preferences.fontSize - INPUT_OFFSET);
    }

    private static void setMarginAndWidthAddEdit(View v) {
        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) v.getLayoutParams();
        cl.leftMargin = width / 20;
        cl.width = width - (width / 10);
    }

    private static void formatHome(Activity activity) {

    }
}
