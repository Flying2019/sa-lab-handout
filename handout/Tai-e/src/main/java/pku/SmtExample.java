package pku;

import java.math.BigInteger;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

/** Executable examples of expression construction, independent of the analyzer. */
public final class SmtExample {
    public static void main(String[] args) throws Exception {
        try (var context = SolverContextFactory.createSolverContext(Solvers.Z3)) {
            lectureArithmetic(context);
            normalReturn(context);
            arrays(context);
            signedArithmetic(context);
        }
    }

    // slides13: x + 2*y = 20, x - y = 2, using mathematical integers.
    private static void lectureArithmetic(SolverContext context) throws Exception {
        var im = context.getFormulaManager().getIntegerFormulaManager();
        var x = im.makeVariable("math_x");
        var y = im.makeVariable("math_y");
        try (var prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
            prover.addConstraint(im.equal(im.add(x, im.multiply(im.makeNumber(2), y)),
                    im.makeNumber(20)));
            prover.push(im.equal(im.subtract(x, y), im.makeNumber(2)));
            require(!prover.isUnsat(), "lecture arithmetic must be SAT");
            try (var model = prover.getModel()) {
                require(BigInteger.valueOf(8).equals(model.evaluate(x)), "x must be 8");
                require(BigInteger.valueOf(6).equals(model.evaluate(y)), "y must be 6");
                System.out.println("lecture arithmetic: x = 8, y = 6");
            }
            prover.pop();
            prover.push(im.equal(im.subtract(x, y), im.makeNumber(3)));
            require(prover.isUnsat(), "second integer branch must be UNSAT");
            System.out.println("lecture alternative: unsat");
        }
    }

    private static void normalReturn(SolverContext context) throws Exception {
        var fm = context.getFormulaManager();
        var bv = fm.getBitvectorFormulaManager();
        var bool = fm.getBooleanFormulaManager();
        var x = bv.makeVariable(32, "x");
        var d = bv.makeVariable(32, "d");
        var zero = bv.makeBitvector(32, 0);
        try (var prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
            for (var input : new org.sosy_lab.java_smt.api.BitvectorFormula[]{x, d}) {
                prover.addConstraint(bv.greaterOrEquals(input, bv.makeBitvector(32, -16), true));
                prover.addConstraint(bv.lessOrEquals(input, bv.makeBitvector(32, 16), true));
            }
            prover.addConstraint(bool.not(bv.equal(d, zero)));
            prover.push(bv.greaterThan(x, zero, true));
            require(!prover.isUnsat(), "normal path must be SAT");
            try (var model = prover.getModel()) {
                int xv = model.evaluate(x).intValue();
                int dv = model.evaluate(d).intValue();
                require(xv > 0 && xv <= 16 && dv >= -16 && dv <= 16 && dv != 0,
                        "invalid model");
                System.out.println("normal path: sat, x = " + xv + ", d = " + dv);
            }
            prover.pop();
            prover.push(bv.equal(d, zero));
            require(prover.isUnsat(), "zero divisor must be UNSAT");
            System.out.println("zero divisor path: unsat");
        }
    }

    private static void arrays(SolverContext context) throws Exception {
        var fm = context.getFormulaManager();
        var bv = fm.getBitvectorFormulaManager();
        var bool = fm.getBooleanFormulaManager();
        var am = fm.getArrayFormulaManager();
        var type = FormulaType.getBitvectorTypeWithSize(32);
        var a = am.makeArray("a", type, type);
        var i = bv.makeVariable(32, "i");
        var two = bv.makeBitvector(32, 2);
        var written = am.store(a, i, two);
        try (var prover = context.newProverEnvironment()) {
            prover.addConstraint(bv.greaterOrEquals(i, bv.makeBitvector(32, 0), true));
            prover.addConstraint(bv.lessThan(i, bv.makeBitvector(32, 3), true));
            prover.addConstraint(bool.not(bv.equal(am.select(written, i), two)));
            require(prover.isUnsat(), "read after write must equal written value");
            System.out.println("array write counterexample: unsat");
        }
    }

    private static void signedArithmetic(SolverContext context) throws Exception {
        var fm = context.getFormulaManager();
        var bv = fm.getBitvectorFormulaManager();
        var bool = fm.getBooleanFormulaManager();
        var minusFive = bv.makeBitvector(32, -5);
        var two = bv.makeBitvector(32, 2);
        var facts = bool.and(
                bv.equal(bv.divide(minusFive, two, true), bv.makeBitvector(32, -2)),
                bv.equal(bv.remainder(minusFive, two, true), bv.makeBitvector(32, -1)),
                bv.equal(bv.add(bv.makeBitvector(32, Integer.MAX_VALUE),
                        bv.makeBitvector(32, 1)), bv.makeBitvector(32, Integer.MIN_VALUE)));
        try (var prover = context.newProverEnvironment()) {
            prover.addConstraint(bool.not(facts));
            require(prover.isUnsat(), "Java int semantics mismatch");
            System.out.println("signed int semantics: pass");
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
