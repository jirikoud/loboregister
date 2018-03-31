package cz.jksoftware.loboregister.infrastructure;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Koudy on 3/31/2018.
 * View utilities
 */

public class ViewUtils {

    public static void hideKeyboard(Context context, View view) {
        if (context == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null && manager.isAcceptingText()) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
