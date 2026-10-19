package a2;
import benchmark.internal.Benchmark;
public class VariableOrder {
    static void compare(int x, int y) {
        if (x <= y) Benchmark.testSign(1, x);
        if (y >= x) Benchmark.testSign(2, x);
    }
    public static void main(String[] args) {
        compare(args.length - 2, -1);
    }
}
