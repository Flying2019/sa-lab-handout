package a5;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class BranchNegative {
    static A choose(A a, A b, A c, int n, int m) {
        Benchmark.testSign(1, n);
        if (n <= m) {
            Benchmark.testSign(3, n);
            if (n >= 0) {
                return c;
            }
            return b;
        }
        Benchmark.testSign(2, n);
        if (n > 0) {
            return a;
        }
        Benchmark.testSign(4, n);
        return a;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        int n = args.length - 1;
        A x = choose(a, b, c, n, -1);
        A y = choose(a, b, c, -2, -1);
        A z = choose(a, b, c, 0, -1);
        Benchmark.test(5, x);
        Benchmark.test(6, y);
        Benchmark.test(7, z);
    }
}
