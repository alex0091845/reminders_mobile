package org.chowmein.reminders.model.themes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;

public class UnicornTheme extends Theme {
    public static Drawable[] itemDrawableArray = null;

    @Override
    public int getStatusBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornAqua);
    }

    @Override
    public int getHeaderColor(Context context) {
        return getStatusBarColor(context);
    }

    @Override
    public Drawable[] getItemDrawableArray(Context context) {
        if(itemDrawableArray == null) {
            itemDrawableArray = new Drawable[]{
                    ContextCompat.getDrawable(context, R.drawable.ripple_unicorn_salmon),
                    ContextCompat.getDrawable(context, R.drawable.ripple_unicorn_limegreen),
                    ContextCompat.getDrawable(context, R.drawable.ripple_unicorn_pink),
                    ContextCompat.getDrawable(context, R.drawable.ripple_unicorn_lemon)
            };
        }
        return itemDrawableArray;
    }

    @Override
    public Drawable getButtonPositiveDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_unicorn_lightpurple);
    }

    @Override
    public int getFabColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornLightPurple);
    }

    @Override
    public int getSeekBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornLimeGreen);
    }

    @Override
    public int getTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornPink);
    }

    @Override
    public int getDescTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornPink);
    }

    @Override
    public int getDateTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornSalmon);
    }

    @Override
    public int getDbrTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornLightPurple);
    }

    @Override
    public int getThemeStyle() {
        return R.style.Unicorn;
    }

    @Override
    public int getEdgeEffectColor(Context context) {
        return ContextCompat.getColor(context, R.color.unicornAqua);
    }
}
