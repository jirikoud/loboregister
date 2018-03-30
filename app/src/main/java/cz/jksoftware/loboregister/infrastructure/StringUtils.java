package cz.jksoftware.loboregister.infrastructure;

/**
 * Created by Koudy on 3/30/2018.
 * String utilities
 */

public class StringUtils {

    public static boolean isNullOrWhiteSpace(String string) {
        return string == null || string.trim().length() == 0;
    }
}
