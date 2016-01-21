package lt.vilnius.tvarkau.entity;

import java.util.List;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public class IssuesResponse {
    // TODO implement other issue fields

    List<Problem> entries;

    public List<Problem> getProblems() {
        return entries;
    }
}
