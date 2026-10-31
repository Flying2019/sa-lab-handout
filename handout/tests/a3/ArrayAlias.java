package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class ArrayAlias {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        A[] e = new A[2];
        A[] f = e;
        Benchmark.alloc(5);
        A[] g = new A[2];
        Benchmark.alloc(6);
        A[] h = new A[2];
        e[0] = a;
        f[1] = b;
        g[0] = c;
        h[0] = a;
        Benchmark.test(1, e[0]);
        Benchmark.test(2, f[1]);
        Benchmark.test(3, g[0]);
        Benchmark.test(4, h[0]);
        Benchmark.test(5, e[1]);
        Benchmark.test(6, f[0]);
    }
}
