package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.D;

public class ArrayField {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        D e = new D();
        Benchmark.alloc(5);
        D f = new D();
        Benchmark.alloc(6);
        e.p = new A[2];
        Benchmark.alloc(7);
        f.p = new A[2];
        A[] h = e.p;
        h[0] = a;
        e.p[1] = b;
        f.p[0] = c;
        f.p[1] = c;
        int i = 0;
        if (args.length > 0) {
            i = 1;
        }
        Benchmark.test(5, e.p[i]);
        Benchmark.test(2, f.p[i]);
        Benchmark.test(9, h[1]);
    }
}
