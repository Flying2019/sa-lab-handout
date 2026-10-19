package a2;
import benchmark.internal.Benchmark;
public class CallerArgumentOrder {
    static int difference(int a, int b) { return a - b; }
    public static void main(String[] args) {
        int x = difference(-2, 0);
        Benchmark.testSign(1, x);
    }
}
