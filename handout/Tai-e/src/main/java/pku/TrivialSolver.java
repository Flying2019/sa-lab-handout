package pku;

import java.util.Set;
import java.util.TreeSet;

/** Runnable protocol baseline, deliberately imprecise; not a reference solution. */
public final class TrivialSolver {
    public CourseResult solve(String profile, Preprocess input) {
        if (!Set.of("a1", "a2", "a3", "a4", "a5").contains(profile)) {
            throw new IllegalArgumentException("Unknown profile: " + profile);
        }
        var all = new TreeSet<Integer>();
        input.allocations.forEach(a -> all.add(a.id()));
        var result = new CourseResult();
        input.queries.values().forEach(q -> {
            if (q.sign()) result.sign(q.id(), "+-0");
            else result.pointsTo(q.id(), all);
        });
        return result;
    }
}
