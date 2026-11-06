package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class ReturnRoutes {
    static A get(A a, A b, int n) {
        if (n > 0) {
            return a;
        }
        return b;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        A d = new A();
        A x = get(a, b, args.length);
        A y = get(c, d, args.length);
        Benchmark.test(1, x);
        Benchmark.test(2, y);
    }
}
