package a5;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.D;
import benchmark.objects.E;
import benchmark.objects.F;

public class BufferSnapshot {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        D x = new D();
        Benchmark.alloc(5);
        A[] r = new A[2];
        x.p = r;
        r[0] = a;
        r[1] = b;
        A[] s = x.p;
        D y = x;
        Benchmark.alloc(6);
        A[] t = new A[2];
        y.p = t;
        t[0] = c;
        t[1] = a;
        E.q = r;
        F.q = t;
        Benchmark.test(1, s[0]);
        Benchmark.test(2, x.p[0]);
        Benchmark.test(3, s[1]);
        Benchmark.test(4, x.p[1]);
        Benchmark.test(5, s);
        Benchmark.test(6, x.p);
        Benchmark.test(7, E.q[0]);
        Benchmark.test(8, F.q[0]);
    }
}
