package cz.jksoftware.loboregister.infrastructure;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Koudy on 3/30/2018.
 * String utilities
 */

public class StringUtils {

    public static boolean isNullOrWhiteSpace(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static Date parseDate(@NonNull DateFormat dateFormat, String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        try {
            Date date = dateFormat.parse(string);
            return date;
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String formatDate(@NonNull DateFormat dateFormat, Date date){
        if (date == null){
            return null;
        }
        return dateFormat.format(date);
    }

    public static boolean isEqual(String string1, String string2) {
        return string1 == null && string2 == null || !(string1 == null || string2 == null) && string1.equals(string2);
    }
}
