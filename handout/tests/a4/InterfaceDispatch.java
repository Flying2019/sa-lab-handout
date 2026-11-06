package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.I;
import benchmark.objects.M;
import benchmark.objects.J;
import benchmark.objects.K;
import benchmark.objects.L;

public class InterfaceDispatch {
    static A g(I b, A a) {
        return b.f(a);
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(4);
        A b = new A();
        Benchmark.alloc(5);
        A c = new A();
        Benchmark.alloc(6);
        A d = new A();
        J.p = b;
        L.set(c);
        M.p = d;
        Benchmark.alloc(2);
        I x = new J();
        Benchmark.alloc(3);
        I y = new K();
        I z = x;
        if (args.length > 0) {
            z = y;
        }
        A e = g(z, a);
        Benchmark.test(1, e);
        Benchmark.test(2, z);
    }
}
