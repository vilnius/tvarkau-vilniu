package lt.vilnius.tvarkau.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martynas Jurkus
 */

public class TextUtils {

    private final static String PROBLEM_ID_REGEXP = "E50-\\S+\\)";

    private TextUtils() {
    }

    public static List<String> findProblemIdOccurrences(String source) {
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile(PROBLEM_ID_REGEXP, Pattern.CASE_INSENSITIVE)
                .matcher(source);

        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }
}
