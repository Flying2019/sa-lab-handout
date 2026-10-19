package a2;
import benchmark.internal.Benchmark;
public class MultiCaller {
    static int negate(int x) {
        Benchmark.testSign(1, x);
        x = -x;
        return x;
    }
    public static void main(String[] args) {
        int x = negate(-3);
        int y = negate(0);
        int z = x + y;
        Benchmark.testSign(2, z);
    }
}
