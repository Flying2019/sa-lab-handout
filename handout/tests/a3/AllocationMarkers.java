package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class AllocationMarkers {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A b = new A();
        Benchmark.alloc(2);
        A c = new A();
        Benchmark.alloc(3);
        A[] d = new A[1];
        Benchmark.alloc(4);
        A[] e = new A[1];
        Benchmark.alloc(5);
        A[] f = new A[1];
        d[0] = b;
        e[0] = c;
        f[0] = d[0];
        Benchmark.test(10, c);
        Benchmark.test(4, d);
        Benchmark.test(21, f);
        Benchmark.test(7, f[0]);
        Benchmark.test(33, e[0]);
        Benchmark.test(2, e);
    }
}
