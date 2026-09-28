package pku;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.ir.stmt.New;

/** Records markers; reachability and propagation belong to the student's solver. */
public final class Preprocess {
    public record Allocation(IR ir, New statement, int id) {}
    public record Query(IR ir, Invoke statement, int id, Var value, boolean sign) {}
    public final List<Allocation> allocations = new ArrayList<>();
    public final Map<Integer, Query> queries = new TreeMap<>();

    public void scan(IR ir) {
        Integer pending = null;
        for (var statement : ir.getStmts()) {
            if (statement instanceof New allocation && pending != null) {
                allocations.add(new Allocation(ir, allocation, pending));
                pending = null;
            }
            if (!(statement instanceof Invoke call) || !call.isStatic()) continue;
            var exp = call.getInvokeExp();
            var method = exp.getMethodRef();
            if (!method.getDeclaringClass().getName().equals("benchmark.internal.Benchmark")) continue;
            String name = method.getName();
            if (!name.equals("alloc") && !name.equals("test") && !name.equals("testSign")) continue;
            int id = ((IntLiteral) exp.getArg(0).getConstValue()).getValue();
            if (name.equals("alloc")) {
                if (pending != null) throw new IllegalArgumentException("Unbound alloc marker");
                pending = id;
            } else {
                var query = new Query(ir, call, id, exp.getArg(1), name.equals("testSign"));
                if (queries.putIfAbsent(id, query) != null) {
                    throw new IllegalArgumentException("Duplicate query " + id);
                }
            }
        }
        if (pending != null) throw new IllegalArgumentException("Unbound alloc marker");
    }
}
