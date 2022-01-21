package group18.payment.utils;

import java.util.Arrays;

/**
 * @Author Aidana
 */

public class StringUtils {

    public static boolean NullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean AnyNullOrEmpty(String... values) {
        return Arrays.stream(values).anyMatch(s -> s == null || s.isEmpty());
    }

}
