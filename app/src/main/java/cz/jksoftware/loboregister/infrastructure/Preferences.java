package cz.jksoftware.loboregister.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jiří Koudelka on 27.03.2018.
 * App preferences handling
 */

public class Preferences {

    @SuppressWarnings("unused")
    private static final String TAG = "Preferences";

    private static final String PREFERENCES_NAME = "LOBOREG_PREFERENCES";
    private static final String KEY_INTRO_ALERT = "IntroAlert";

    private static Preferences mInstance;

    private boolean mIntroAlert;

    private Preferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        mIntroAlert = preferences.getBoolean(KEY_INTRO_ALERT, false);
    }

    public static Preferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Preferences(context);
        }
        return mInstance;
    }

    public boolean getIntroAlert() {
        return mIntroAlert;
    }

    public void setIntroAlert(Context context, boolean introAlert) {
        mIntroAlert = introAlert;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_INTRO_ALERT, mIntroAlert);
        editor.apply();
    }

}
