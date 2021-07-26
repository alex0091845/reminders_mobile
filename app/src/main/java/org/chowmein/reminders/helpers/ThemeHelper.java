package org.chowmein.reminders.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.chowmein.reminders.R;
import org.chowmein.reminders.managers.Preferences;

/**
 *
 */
public class ThemeHelper {

    public static final String RED_VELVET = "Red Velvet";
    public static final String MANGO = "Mango";
    public static final String OCEAN = "Ocean";
    public static final String GRAPE = "Grape";
    public static final String FOREST = "Forest";
    public static final String SHERBET = "Sherbet";
    public static final String UNICORN = "Unicorn";

    public static int getThemeStyle() {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return R.style.AppTheme;
//            case MANGO:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            case OCEAN:
                return R.style.Ocean;
//            case GRAPE:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            default:
                return -1;
        }
    }

    public static int getThemeColorAccent(Context context) {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case MANGO:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            case OCEAN:
                return ContextCompat.getColor(context, R.color.oceanAccent);
//            case GRAPE:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            default:
                return -1;
        }
    }

    public static int getThemeColorPrimary(Context context) {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return ContextCompat.getColor(context, R.color.redVelvetPrimary);
//            case MANGO:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            case OCEAN:
                return ContextCompat.getColor(context, R.color.oceanPrimary);
//            case GRAPE:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            default:
                return -1;
        }
    }

    public static int getThemeColorPrimaryDark(Context context) {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return ContextCompat.getColor(context, R.color.redVelvetPrimaryDark);
//            case MANGO:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            case OCEAN:
                return ContextCompat.getColor(context, R.color.oceanPrimaryDark);
//            case GRAPE:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getColor(context, R.color.redVelvetAccent);
            default:
                return -1;
        }
    }

    public static Drawable getRippleDrawablePrimaryDark(Context context) {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return ContextCompat.getDrawable(context, R.drawable.ripple_red_velvet_dark);
//            case MANGO:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
            case OCEAN:
                return ContextCompat.getDrawable(context, R.drawable.ripple_ocean_dark);
//            case GRAPE:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
            default:
                return null;
        }
    }

    public static Drawable getRippleDrawablePrimary(Context context) {
        String theme = Preferences.getTheme();

        switch(theme) {
            case RED_VELVET:
                return ContextCompat.getDrawable(context, R.drawable.ripple_red_velvet);
//            case MANGO:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
            case OCEAN:
                return ContextCompat.getDrawable(context, R.drawable.ripple_ocean);
//            case GRAPE:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case FOREST:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case SHERBET:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
//            case UNICORN:
//                return ContextCompat.getDrawable(context, R.drawable.redVelvetAccent);
            default:
                return null;
        }
    }

    public static Drawable getRippleDrawableGray(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ripple_gray);
    }

    public static Drawable getBackgroundDrawableBlue(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.item_bg_blue);
    }

    public static Drawable getRippleDrawableButtonPositive(Context context) {
        return getRippleDrawablePrimaryDark(context);
    }

    public static Drawable getRippleDrawableButtonNegative(Context context) {
        return getRippleDrawableGray(context);
    }

    public static int getColorWhite(Context context) {
        return ContextCompat.getColor(context, R.color.white);
    }

    public static int getColorDarkGray(Context context) {
        return ContextCompat.getColor(context, R.color.darkGray);
    }

    public static Drawable getSelectedEventDrawable(Context context) {
        return getBackgroundDrawableBlue(context);
    }

}