package a1;
import benchmark.internal.Benchmark;
public class ReuseAfterKill {
    public static void main(String[] args) {
        int x = args.length - 4;
        int y = x * x;
        Benchmark.testSign(1, y);
        x = 0;
        y = x * y;
        Benchmark.testSign(2, y);
        x = -5;
        x = x + 1;
        Benchmark.testSign(3, x);
    }
}
