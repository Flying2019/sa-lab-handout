package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.D;

public class StaticArrayBridge {
    static D c;
    static A[] d;
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A f = new A();
        Benchmark.alloc(4);
        c = new D();
        Benchmark.alloc(5);
        c.p = new A[2];
        c.q = f;
        d = c.p;
        d[0] = a;
        A[] h = c.p;
        h[1] = b;
        Benchmark.test(1, c.p[0]);
        Benchmark.test(2, d[1]);
        Benchmark.test(3, c.q);
    }
}
