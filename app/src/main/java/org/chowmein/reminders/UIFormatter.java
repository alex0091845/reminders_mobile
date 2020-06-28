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

class UIFormatter {

    final static int HOME = 0;
    final static int ADD = 1;
    final static int EDIT = 2;

    static int width;
    static int height;

    static void format(Activity activity, int activityId) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        if(activityId == HOME) formatHome(activity);
        else if(activityId == ADD) formatAdd(activity);
        else if(activityId == EDIT) formatEdit(activity);
    }

    private static void formatEdit(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date_edit);
        setMarginAndWidth(tvDate);
        Button btnDate = activity.findViewById(R.id.btn_date_edit);
        setMarginAndWidth(btnDate);
        TextView tvDesc = activity.findViewById(R.id.tv_desc_edit);
        setMarginAndWidth(tvDesc);
        EditText edtDesc = activity.findViewById(R.id.edt_desc_edit);
        setMarginAndWidth(edtDesc);
        EditText edtDbr = activity.findViewById(R.id.edt_dbr_edit);
        setMarginAndWidth(edtDbr);
    }

    private static void formatAdd(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date);
        setMarginAndWidth(tvDate);
        Button btnDate = activity.findViewById(R.id.btn_date);
        setMarginAndWidth(btnDate);
        TextView tvDesc = activity.findViewById(R.id.tv_desc);
        setMarginAndWidth(tvDesc);
        EditText edtDesc = activity.findViewById(R.id.edt_desc);
        setMarginAndWidth(edtDesc);
        EditText edtDbr = activity.findViewById(R.id.edt_dbr);;
        setMarginAndWidth(edtDbr);
    }

    private static void setMarginAndWidth(View v) {
        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) v.getLayoutParams();
        cl.leftMargin = width / 20;
        cl.width = width - (width / 10);
    }

    private static void formatHome(Activity activity) {

    }
}
