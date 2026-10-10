package pku;

import java.util.TreeMap;
import java.util.TreeSet;
import pascal.taie.World;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.stmt.Invoke;

/** Runnable protocol baseline, deliberately imprecise; not a reference solution. */
public final class TrivialSolver {
    public CourseResult solve() {
        var all = new TreeSet<Integer>();
        var queries = new TreeMap<Integer, Boolean>();
        World.get().getClassHierarchy().applicationClasses().forEach(c -> {
            if (c.getName().equals("benchmark.internal.Benchmark")) return;
            for (var method : c.getDeclaredMethods()) {
                if (method.isAbstract() || method.isNative()) continue;
                for (var statement : method.getIR()) {
                    if (!(statement instanceof Invoke call)) continue;
                    var exp = call.getInvokeExp();
                    var ref = exp.getMethodRef();
                    if (!ref.getDeclaringClass().getName().equals("benchmark.internal.Benchmark")) continue;
                    int id = ((IntLiteral) exp.getArg(0).getConstValue()).getValue();
                    switch (ref.getName()) {
                        case "alloc" -> all.add(id);
                        case "testSign" -> queries.put(id, true);
                        case "test" -> queries.put(id, false);
                    }
                }
            }
        });
        var result = new CourseResult();
        queries.forEach((id, sign) -> {
            if (sign) result.sign(id, "+-0");
            else result.pointsTo(id, all);
        });
        return result;
    }
}
