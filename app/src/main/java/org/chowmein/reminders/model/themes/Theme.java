package org.chowmein.reminders.model.themes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;

public abstract class Theme {
    public static int[] itemColorArray = null;

    public abstract int getStatusBarColor(Context context);
    public abstract int getHeaderColor(Context context);
    public abstract Drawable[] getItemDrawableArray(Context context);
    public abstract Drawable getButtonPositiveDrawable(Context context);
    public abstract int getFabColor(Context context);
    public abstract int getSeekBarColor(Context context);
    public abstract int getTextViewColor(Context context);
    public abstract int getThemeStyle();

    public Drawable getButtonNegativeDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_gray);
    }

    public int getDescTextViewColor(Context context) {
        return getTextViewColor(context);
    }

    public int getDateTextViewColor(Context context) {
        return getTextViewColor(context);
    }

    public int getDbrTextViewColor(Context context) {
        return getTextViewColor(context);
    }

    public static int[] getItemColorArray(Context context) {
        if(itemColorArray == null) {
            itemColorArray = new int[] {
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.darkGray)
            };
        }
        return itemColorArray;
    }

    public static Drawable getSelectedEventDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.item_bg_blue);
    }
}
