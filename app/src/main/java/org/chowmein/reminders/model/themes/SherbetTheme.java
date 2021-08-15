package org.chowmein.reminders.model.themes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;

public class SherbetTheme extends Theme {
    public static Drawable[] itemDrawableArray = null;

    @Override
    public int getStatusBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetPurple);
    }

    @Override
    public int getHeaderColor(Context context) {
        return getStatusBarColor(context);
    }

    @Override
    public Drawable[] getItemDrawableArray(Context context) {
        if(itemDrawableArray == null) {
            itemDrawableArray = new Drawable[]{
                    ContextCompat.getDrawable(context, R.drawable.ripple_sherbet_blue),
                    ContextCompat.getDrawable(context, R.drawable.ripple_sherbet_lightpink),
                    ContextCompat.getDrawable(context, R.drawable.ripple_sherbet_lightorange),
                    ContextCompat.getDrawable(context, R.drawable.ripple_sherbet_lightgreen)
            };
        }
        return itemDrawableArray;
    }

    @Override
    public Drawable getButtonPositiveDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_sherbet_salmon);
    }

    @Override
    public int getFabColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetSalmon);
    }

    @Override
    public int getSeekBarColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetOrange);
    }

    @Override
    public int getTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetSalmon);
    }

    @Override
    public int getDescTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetBlue);
    }

    @Override
    public int getDateTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetOrange);
    }

    @Override
    public int getDbrTextViewColor(Context context) {
        return ContextCompat.getColor(context, R.color.sherbetGreen);
    }

    @Override
    public int getThemeStyle() {
        return R.style.Sherbet;
    }
}
