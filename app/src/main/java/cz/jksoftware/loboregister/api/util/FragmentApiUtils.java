package cz.jksoftware.loboregister.api.util;

import android.app.Activity;
import android.widget.Toast;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ErrorModel;

/**
 * Created by Jiří Koudelka on 28.03.2018.
 * Utility for API procession in fragments
 */

public class FragmentApiUtils {

    public static boolean processTaskResponse(LoadStatus loadStatus, Activity activity, ErrorModel errorModel) {
        if (activity == null) {
            return true;
        }
        switch (loadStatus) {
            case SUCCESS:
                break;
            case OFFLINE:
                Toast.makeText(activity, activity.getString(R.string.api_toast_offline), Toast.LENGTH_LONG).show();
                return true;
            case FAILED:
                Toast.makeText(activity, activity.getString(R.string.api_toast_connection_failed), Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }
}
