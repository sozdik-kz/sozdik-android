package kz.sozdik.core;

import android.text.TextUtils;

import com.pixplicity.easyprefs.library.Prefs;

public class AuthUtils {

    private static final String TOKEN = "token";

    public static void clear() {
        setAuthToken(null);
    }

    public static String getAuthToken() {
        return Prefs.getString(TOKEN, "");
    }

    public static void setAuthToken(String token) {
        Prefs.putString(TOKEN, token);
    }

    public static State getState() {
        if (TextUtils.isEmpty(getAuthToken())) {
            return State.NOT_AUTHORIZED;
        }

        return State.AUTHORIZED;
    }

    public static boolean isAuthorized() {
        return getState() == State.AUTHORIZED;
    }

    public enum State {
        AUTHORIZED,
        NOT_AUTHORIZED
    }
}
