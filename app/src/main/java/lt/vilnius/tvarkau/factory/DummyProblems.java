package lt.vilnius.tvarkau.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lt.vilnius.tvarkau.entity.Problem;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:30.
 */
public class DummyProblems {
    public static final int COUNT = 100;
    private static List<Problem> problems;
    protected static final String[] thumbsUrls = {
            "http://cssslider.com/sliders/demo-26/data1/images/summerfield336672_1280.jpg",
            "http://drop.ndtv.com/albums/AUTO/mercedesbenzcla/3-main_640x480.jpg",
            null
    };

    protected static final String[] adresses = {
            "J.Jasinskio g. 5",
            "Konstitucijos pr. 3"
    };

    public static List<Problem> getProblems() {
        if (problems == null) problems = createProblems();
        return problems;
    }

    public static List<Problem> createProblems() {
        ArrayList<Problem> problems = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < COUNT; i++) {
            Problem problem = new Problem();

            problem.id = i;
            problem.title = "Gatvių priežiūra ir tvarkymas";
            problem.description = "Klinikų g. jau du mėnesiai nenaudojama pilnai pilnai įrengta automobilių parkavimo....";

            if (Math.random() < 0.5) {
                problem.statusCode = Problem.STATUS_IN_PROGRESS;
                problem.statusDescription = "Vykdoma";
            } else {
                problem.statusCode = Problem.STATUS_DONE;
                problem.statusDescription = "Atlikta";
            }
            problem.updatedAt = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis((int) (Math.random() * 20) + 5));

            problem.lat = 54.5 + 0.4 * Math.random();
            problem.lng = 25.1 + 0.4 * Math.random();

            problem.thumbUrl = thumbsUrls[random.nextInt(thumbsUrls.length)];
            problem.address = adresses[random.nextInt(adresses.length)];

            problems.add(problem);
        }

        return problems;
    }
}
