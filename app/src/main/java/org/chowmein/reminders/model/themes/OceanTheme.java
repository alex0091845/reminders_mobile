package org.chowmein.reminders.model.themes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;

public class OceanTheme extends Theme {
    public static Drawable[] itemDrawableArray = null;

    @Override
    public int getStatusBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.oceanPrimaryDark);
    }

    @Override
    public int getHeaderColor(Context context) {
        return getStatusBarColor(context);
    }

    @Override
    public Drawable[] getItemDrawableArray(Context context) {
        if(itemDrawableArray == null) {
            itemDrawableArray = new Drawable[]{
                    ContextCompat.getDrawable(context, R.drawable.ripple_ocean),
                    ContextCompat.getDrawable(context, R.drawable.ripple_gray)
            };
        }
        return itemDrawableArray;
    }

    @Override
    public Drawable getButtonPositiveDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_ocean_dark);
    }

    @Override
    public int getFabColor(Context context) {
        return ContextCompat.getColor(context, R.color.oceanPrimaryDark);
    }

    @Override
    public int getSeekBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.oceanAccent);
    }

    @Override
    public int getTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.oceanPrimaryDark);
    }

    @Override
    public int getThemeStyle() {
        return R.style.Ocean;
    }

    @Override
    public int getEdgeEffectColor(Context context) {
        return ContextCompat.getColor(context, R.color.oceanPrimary);
    }
}
