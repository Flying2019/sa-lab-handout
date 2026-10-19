package a2;
import benchmark.internal.Benchmark;
public class VariableNegation {
    static void upper(int x, int y) {
        if (x > y) return;
        Benchmark.testSign(1, x);
    }
    static void reversed(int x, int y) {
        if (y < x) return;
        Benchmark.testSign(2, x);
    }
    static int lower(int x, int y) {
        if (x < y) return y;
        Benchmark.testSign(3, x);
        return x;
    }
    public static void main(String[] args) {
        int x = args.length - 2;
        upper(x, -1);
        reversed(x, -1);
        int result = lower(x, 1);
        Benchmark.testSign(4, result);
    }
}
