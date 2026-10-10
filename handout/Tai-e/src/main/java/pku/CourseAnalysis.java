package pku;

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
        var result = new TrivialSolver().solve();
        result.write();
        return result;
    }
}
