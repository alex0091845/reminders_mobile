package org.chowmein.reminders.model.themes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;

public class MangoTheme extends Theme {
    public static Drawable[] itemDrawableArray = null;

    @Override
    public int getStatusBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.mangoPrimaryDark);
    }

    @Override
    public int getHeaderColor(Context context) {
        return getStatusBarColor(context);
    }

    @Override
    public Drawable[] getItemDrawableArray(Context context) {
        if(itemDrawableArray == null) {
            itemDrawableArray = new Drawable[]{
                    ContextCompat.getDrawable(context, R.drawable.ripple_mango),
                    ContextCompat.getDrawable(context, R.drawable.ripple_gray)
            };
        }
        return itemDrawableArray;
    }

    @Override
    public Drawable getButtonPositiveDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_mango_dark);
    }

    @Override
    public int getFabColor(Context context) {
        return ContextCompat.getColor(context, R.color.mangoPrimaryDark);
    }

    @Override
    public int getSeekBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.mangoAccent);
    }

    @Override
    public int getTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.mangoPrimaryDark);
    }

    @Override
    public int getThemeStyle() {
        return R.style.Mango;
    }

    @Override
    public int getEdgeEffectColor(Context context) {
        return ContextCompat.getColor(context, R.color.mangoPrimary);
    }
}
