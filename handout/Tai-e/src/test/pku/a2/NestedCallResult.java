package a2;
import benchmark.internal.Benchmark;
public class NestedCallResult {
    static int outer(int x) { return -bridge(-x); }
    static int bridge(int x) { return x * 2; }
    public static void main(String[] args) {
        int x = outer(3);
        Benchmark.testSign(1, x);
    }
}
