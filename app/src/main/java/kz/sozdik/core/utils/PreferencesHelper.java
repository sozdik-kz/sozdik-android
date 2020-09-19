package kz.sozdik.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import timber.log.Timber;

// TODO: Refactor
public class PreferencesHelper {
    private static final String SETTINGS = "settings";
    private static final String TAG_DISABLED_ADS = "TAG_DISABLED_ADS";

    private static boolean disabledADS;

    public static boolean isAdsDisabled() {
        Timber.d("disabledADS = " + disabledADS);
        return disabledADS;
    }

    public static void loadSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, 0);
        if (preferences.getAll().size() == 0) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            disabledADS = false;
        } else {
            disabledADS = preferences.getBoolean(TAG_DISABLED_ADS, false);
        }
    }

    public enum Purchase {
        DISABLE_ADS
    }
}
