package org.chowmein.reminders.managers;

/*
 * ----------------------------------------References-----------------------------------------------
 * Get screen width and height pixels:
 * https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
 * Setting constraint layout margins:
 * https://stackoverflow.com/questions/52148129/programmatically-set-margin-to-constraintlayout
 * Setting seekbar progress color:
 * https://stackoverflow.com/questions/45329174/how-to-change-seekbar-color-in-android-programmatically
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.chowmein.reminders.R;
import org.chowmein.reminders.activities.HomeActivity;
import org.chowmein.reminders.activities.SettingsActivity;
import org.chowmein.reminders.components.FontSizePreference;
import org.chowmein.reminders.components.GeneralPreference;
import org.chowmein.reminders.components.GeneralPreferenceCategory;
import org.chowmein.reminders.components.ThemePreference;
import org.chowmein.reminders.controller.EventAdapter;
import org.chowmein.reminders.model.Event;

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

        UIFormatter.setStatusBarColor(activity);
        activity.setTheme(Preferences.getTheme().getThemeStyle());

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

    public static void formatButton(Button b, boolean positive, Context context) {
        b.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        if(positive) b.setBackground(Preferences.getTheme().getButtonPositiveDrawable(context));
        else b.setBackground(Preferences.getTheme().getButtonNegativeDrawable(context));
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
        Button btnCancel = activity.findViewById(R.id.btn_cancel_settings);
        Button btnApply = activity.findViewById(R.id.btn_apply_settings);
        formatButton(btnApply, true, activity);
        formatButton(btnCancel, false, activity);

        SettingsActivity settingsActivity = (SettingsActivity) activity;
        View eventView = settingsActivity.findViewById(R.id.list_item);

        RecyclerView rvSettings = settingsActivity.findViewById(R.id.recycler_view);
        if(rvSettings != null) {
            setEdgeEffect(settingsActivity, rvSettings);
        }

        if(eventView != null) {
            View settingsRootView = eventView.getRootView();
            SettingsActivity.SettingsFragment fragment =
                    (SettingsActivity.SettingsFragment) settingsActivity
                            .getSupportFragmentManager()
                            .getFragments()
                            .get(0);

            PreferenceViewHolder fontSizeViewHolder = ((FontSizePreference) fragment.findPreference(Preferences.FONT_SIZE_KEY)).getViewHolder();
            PreferenceViewHolder genPrefCatViewHolder = ((GeneralPreferenceCategory) fragment.findPreference(Preferences.FONT_SIZE_KEY).getParent()).getViewHolder();
            PreferenceViewHolder ringtoneViewHolder = ((GeneralPreference) fragment.findPreference(Preferences.RINGTONE_KEY)).getViewHolder();
            PreferenceViewHolder themeViewHolder = ((ThemePreference) fragment.findPreference(Preferences.THEME_KEY)).getViewHolder();

            formatGenPrefCategory(genPrefCatViewHolder, activity);
            formatFontSizePref(fontSizeViewHolder, activity);
            formatGenPref(ringtoneViewHolder);
            formatGenPref(themeViewHolder);
        }
    }

    private static void setEdgeEffect(Context context, RecyclerView rv) {
        rv.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = super.createEdgeEffect(view, direction);
                edgeEffect.setColor(Preferences.getTheme().getEdgeEffectColor(context));
                return edgeEffect;
            }
        });
    }

    public static void formatGenPrefCategory(PreferenceViewHolder holder, Context context) {
        TextView tv_title = (TextView) holder.findViewById(android.R.id.title);
        UIFormatter.formatTVMedium(tv_title);
        UIFormatter.formatTextViewToTheme(tv_title, context);
    }

    public static void setSeekbarToTheme(SeekBar seekbar, Activity activity) {
        seekbar.setThumbTintList(ColorStateList.valueOf(Preferences.getTheme().getSeekBarColor(activity)));
        seekbar.getProgressDrawable().setColorFilter(Preferences.getTheme().getSeekBarColor(activity), PorterDuff.Mode.MULTIPLY);
    }

    public static void formatGenPref(PreferenceViewHolder view) {
        formatTVMedium(view.findViewById(android.R.id.title));
        formatTVSmol(view.findViewById(android.R.id.summary));
    }

    public static void formatFontSizePref(PreferenceViewHolder view, Context context) {
        formatGenPref(view);
        setSeekbarToTheme((SeekBar)view.findViewById(R.id.seekbar), (Activity) context);

        SettingsActivity settingsActivity = ((SettingsActivity) context);

        formatEventItem(view, Preferences.getFontSize(), (Activity) context);
        formatTVSmall(view.findViewById(R.id.tv_preview));

        TextView tvPreview = (TextView) view.findViewById(R.id.tv_preview);
        formatTVSmall(tvPreview);

        SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        setSeekbarToTheme(seekbar, settingsActivity);
    }

    public static void formatTextViewToTheme(TextView tv, Context context) {
        tv.setTextColor(Preferences.getTheme().getTextViewColor(context));
    }

    public static void formatDateTextViewToTheme(TextView tv, Context context) {
        tv.setTextColor(Preferences.getTheme().getDateTextViewColor(context));
    }

    public static void formatDescTextViewToTheme(TextView tv, Context context) {
        tv.setTextColor(Preferences.getTheme().getDescTextViewColor(context));
    }

    public static void formatDbrTextViewToTheme(TextView tv, Context context) {
        tv.setTextColor(Preferences.getTheme().getDbrTextViewColor(context));
    }

    /**
     * A helper method to format the add/edit Activity, which are awfully similar.
     * @param activity supposedly the add/edit Activity
     */
    private static void formatAddEdit(Activity activity) {
        TextView tvDate = activity.findViewById(R.id.tv_date);
        setMarginAndWidthAddEdit(tvDate);
        tvDate.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);
        formatDateTextViewToTheme(tvDate, activity);

        Button btnDate = activity.findViewById(R.id.btn_date);
        setMarginAndWidthAddEdit(btnDate);
        btnDate.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        TextView tvDesc = activity.findViewById(R.id.tv_desc);
        setMarginAndWidthAddEdit(tvDesc);
        tvDesc.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);
        formatDescTextViewToTheme(tvDesc, activity);

        EditText edtDesc = activity.findViewById(R.id.edt_desc);
        setMarginAndWidthAddEdit(edtDesc);
        edtDesc.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        TextView tvDbr = activity.findViewById(R.id.tv_dbr);
        setMarginAndWidthAddEdit(tvDbr);
        tvDbr.setTextSize(Preferences.getFontSize() - LARGE_OFFSET);
        formatDbrTextViewToTheme(tvDbr, activity);

        EditText edtDbr = activity.findViewById(R.id.edt_dbr);
        setMarginAndWidthAddEdit(edtDbr);
        edtDbr.setTextSize(Preferences.getFontSize() - MEDIUM_OFFSET);

        Button btnSubmit = activity.findViewById(R.id.btn_submit);
        formatButton(btnSubmit, true, activity);

        Button btnCancel = activity.findViewById(R.id.btn_cancel);
        formatButton(btnCancel, false, activity);
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
        Toolbar toolbar = homeActivity.findViewById(R.id.tb_title);
        RecyclerView rvReminders = homeActivity.findViewById(R.id.rv_reminders);
        setEdgeEffect(homeActivity, rvReminders);
        colorHeader(homeActivity, toolbar);
        FloatingActionButton fabAdd = ((HomeActivity)activity).findViewById(R.id.btn_add);
        fabAdd.setBackgroundTintList(ColorStateList.valueOf(Preferences.getTheme().getFabColor(activity)));
        FloatingActionButton fabDelete = ((HomeActivity)activity).findViewById(R.id.btn_delete);
        fabDelete.setImageTintList(ColorStateList.valueOf(Preferences.getTheme().getFabColor(activity)));
    }

    public static void formatEventItem(View eventView, int prefFontSize, Context context) {
        TextView tv_desc = eventView.findViewById(R.id.tv_event_desc);
        tv_desc.setTextSize(prefFontSize);

        TextView tv_date = eventView.findViewById(R.id.tv_event_date);
        tv_date.setTextSize(prefFontSize);

        TextView tv_dbr = eventView.findViewById(R.id.tv_event_dbr);
        tv_dbr.setTextSize(prefFontSize - UIFormatter.SMALL_OFFSET);

        TextView tv_year = eventView.findViewById(R.id.tv_event_year);
        tv_year.setTextSize(prefFontSize - UIFormatter.MEDIUM_OFFSET);

        View list_view = eventView.findViewById(R.id.ll_list_item);
        if(list_view == null) list_view = eventView.findViewById(R.id.list_item);
        ConstraintLayout cl_list_view = list_view.findViewById(R.id.cl_list_item);
        cl_list_view.setBackground(
                Preferences.getTheme().getItemDrawableArray(context)[0]
        );
    }

    public static void formatEventItem(PreferenceViewHolder eventView, int prefFontSize, Context context) {
        TextView tv_desc = (TextView) eventView.findViewById(R.id.tv_event_desc);
        tv_desc.setTextSize(prefFontSize);

        TextView tv_date = (TextView) eventView.findViewById(R.id.tv_event_date);
        tv_date.setTextSize(prefFontSize);

        TextView tv_dbr = (TextView) eventView.findViewById(R.id.tv_event_dbr);
        tv_dbr.setTextSize(prefFontSize - UIFormatter.SMALL_OFFSET);

        TextView tv_year = (TextView) eventView.findViewById(R.id.tv_event_year);
        tv_year.setTextSize(prefFontSize - UIFormatter.MEDIUM_OFFSET);

        View list_view = eventView.findViewById(R.id.ll_list_item);
        if(list_view == null) list_view = eventView.findViewById(R.id.list_item);
        ConstraintLayout cl_list_view = list_view.findViewById(R.id.cl_list_item);
        cl_list_view.setBackground(Preferences.getTheme().getItemDrawableArray(context)[0]);
    }

    public static void colorHeader(Context context, Toolbar toolbar) {
        toolbar.setBackgroundColor(Preferences.getTheme().getHeaderColor(context));
    }

    public static void setStatusBarColor(Activity activity) {
        Window window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Preferences.getTheme().getStatusBarColor(activity));
    }

    public static class EventFormatter {
        /**
         * A helper method to specifically set the colors of the tv_desc, tv_date, and tv_dbr TextViews
         * inside a ViewHolder.
         * @param holder the EventViewHolder
         * @param color the color to assign all TextViews
         */
        private static void setTextViewColors(EventAdapter.EventViewHolder holder, int color) {
            holder.getTvDesc().setTextColor(color);
            holder.getTvDate().setTextColor(color);
            holder.getTvDbr().setTextColor(color);
        }

        public static void styleEvent(Context context, Event event,
                                           EventAdapter.EventViewHolder holder) {
            // indices into respective color/drawable arrays
            int drawableMod = holder.getAdapterPosition() %
                    Preferences.getTheme().getItemDrawableArray(context).length;
            int textColorMod = holder.getAdapterPosition() %
                    Preferences.getTheme().getItemColorArray(context).length;

            // a ViewHolder being selected overrides all of its other colors/styles
            if (event.isSelected()) {
                holder.getClListItem().setBackground(
                        Preferences.getTheme().getSelectedEventDrawable(context)
                );
                setTextViewColors(holder, Preferences.getTheme().getItemColorArray(context)[0]);
            }
            // set style based on position (alternating colors)
            else {
                holder.getClListItem().setBackground(
                        Preferences.getTheme().getItemDrawableArray(context)[drawableMod]
                );
                setTextViewColors(holder,
                        Preferences.getTheme().getItemColorArray(context)[textColorMod]
                );
            }
        }
    }
}
