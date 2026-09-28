package pku;

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.config.AnalysisConfig;

/** Whole-project entry. Students own the analysis design inside this package. */
public class CourseAnalysis extends ProgramAnalysis<CourseResult> {
    public static final String ID = "pku-course";

    public CourseAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public CourseResult analyze() {
        String profile = System.getenv("SA_PROFILE");
        if (profile == null) {
            throw new IllegalStateException("Run through the course adapter (SA_PROFILE is required)");
        }
        var markers = new Preprocess();
        World.get().getClassHierarchy().applicationClasses().forEach(c -> {
            if (!c.getName().equals("benchmark.internal.Benchmark")) {
                c.getDeclaredMethods().forEach(m -> {
                    if (!m.isAbstract() && !m.isNative()) {
                        markers.scan(m.getIR());
                    }
                });
            }
        });
        var result = new TrivialSolver().solve(profile, markers);
        result.write(System.getenv("SA_OUTPUT"));
        return result;
    }
}
