package lt.vilnius.tvarkau.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lt.vilnius.tvarkau.entity.Problem;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:30.
 */
public class DummyProblems {
    public static final int COUNT = 100;
    private static List<Problem> problems;

    public static List<Problem> getProblems() {
        if (problems == null) problems = createProblems();
        return problems;
    }

    public static List<Problem> createProblems() {
        ArrayList<Problem> problems = new ArrayList<>();

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

            problems.add(problem);
        }

        return problems;
    }
}
