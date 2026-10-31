package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.E;
import benchmark.objects.F;

public class StaticNamespaces {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        E.p = a;
        F.p = b;
        Benchmark.alloc(3);
        E.q = new A[1];
        Benchmark.alloc(4);
        F.q = new A[1];
        E.q[0] = F.p;
        F.q[0] = E.p;
        Benchmark.test(1, E.p);
        Benchmark.test(2, F.p);
        Benchmark.test(3, E.q[0]);
        Benchmark.test(4, F.q[0]);
    }
}
