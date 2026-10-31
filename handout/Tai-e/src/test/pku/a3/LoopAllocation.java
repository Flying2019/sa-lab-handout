package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class LoopAllocation {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        B[] d = new B[2];
        Benchmark.alloc(4);
        A[] e = new A[2];
        A f = a;
        Benchmark.alloc(5);
        B g = new B();
        Benchmark.alloc(6);
        B h = new B();
        B j = g;
        for (int i = 0; i < 2; i++) {
            Benchmark.alloc(7);
            B k = new B();
            k.p = a;
            if (i > 0) {
                k.p = b;
            }
            d[i] = k;
            e[i] = f;
            f = b;
            j.p = b;
            j = h;
        }
        Benchmark.test(1, d[0]);
        Benchmark.test(2, d[1].p);
        Benchmark.test(3, e[1]);
        Benchmark.test(4, h.p);
    }
}
