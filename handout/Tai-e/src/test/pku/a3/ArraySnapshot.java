package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class ArraySnapshot {
    static A[] d;
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        d = new A[1];
        d[0] = a;
        A[] f = d;
        Benchmark.alloc(5);
        d = new A[1];
        d[0] = b;
        Benchmark.alloc(6);
        A[] g = new A[1];
        g[0] = c;
        Benchmark.test(1, f[0]);
        Benchmark.test(2, d[0]);
        Benchmark.test(3, g[0]);
    }
}
