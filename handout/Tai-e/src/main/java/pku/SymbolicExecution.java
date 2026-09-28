package pku;

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.stmt.Invoke;
import java.nio.file.*;
import java.util.TreeSet;

/** Runnable entry for the student's whole symbolic-execution project. */
public class SymbolicExecution extends ProgramAnalysis<Void> {
    public static final String ID = "pku-se";
    public SymbolicExecution(AnalysisConfig config) { super(config); }
    @Override public Void analyze() {
        var ids = new TreeSet<Integer>();
        World.get().getClassHierarchy().applicationClasses().forEach(c -> {
            if (!c.getName().startsWith("benchmark.")) c.getDeclaredMethods().forEach(m -> {
                if (!m.isAbstract() && !m.isNative()) for (var stmt : m.getIR()) {
                    if (stmt instanceof Invoke invoke) {
                        var exp = invoke.getInvokeExp();
                        if (exp.getMethodRef().getDeclaringClass().getName().equals("benchmark.internal.Benchmark")
                                && exp.getMethodRef().getName().equals("reach"))
                            ids.add(((IntLiteral) exp.getArg(0).getConstValue()).getValue());
                    }
                }
            });
        });
        // Replace this baseline with your own state exploration and SMT solving.
        // Merely visiting reach is insufficient: continue to normal entry return.
        try {
            Files.write(Path.of(System.getenv("SA_OUTPUT")), ids.stream().map(id -> id + ": unknown").toList());
        } catch (java.io.IOException e) { throw new java.io.UncheckedIOException(e); }
        return null;
    }
}
