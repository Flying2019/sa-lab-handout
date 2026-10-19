package a2;
import benchmark.internal.Benchmark;
public class RecursiveOrder {
    static int adjust(int n, int x, int bound) {
        if (n <= 0) {
            if (x <= bound) {
                Benchmark.testSign(1, x);
                return -x;
            }
            return 0;
        }
        return adjust(n - 1, x, bound);
    }
    public static void main(String[] args) {
        int result = adjust(3, args.length - 2, -1);
        Benchmark.testSign(2, result);
    }
}
