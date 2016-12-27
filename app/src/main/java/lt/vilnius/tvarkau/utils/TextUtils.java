package lt.vilnius.tvarkau.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * @author Martynas Jurkus
 */

public class TextUtils {

    private final static String PROBLEM_ID_REGEXP = ("E50-\\S+?\\)");
    private final static Pattern PROBLEM_ID_PATTERN = Pattern.compile(PROBLEM_ID_REGEXP, CASE_INSENSITIVE);


    private TextUtils() {
    }

    public static List<String> findProblemIdOccurrences(String source) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PROBLEM_ID_PATTERN.matcher(source);

        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }
}
