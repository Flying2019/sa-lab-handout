package a2;
import benchmark.internal.Benchmark;
public class RecursiveCallRoutes {
    static int recurse(int n, int seed) {
        if (n <= 0) return seed;
        int tail = recurse(n - 1, 0);
        Benchmark.testSign(3, tail);
        return seed + tail;
    }
    static int forward(int n, int seed) { return recurse(n, seed); }
    static int left(int n) { return forward(n, 2); }
    static int right(int n) { return forward(n, -3); }
    public static void main(String[] args) {
        int a = left(2);
        int b = right(5);
        int c = forward(4, 0);
        Benchmark.testSign(4, a);
        Benchmark.testSign(5, b);
        Benchmark.testSign(6, c);
    }
}
