package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.I;
import benchmark.objects.T;

public class InheritedCalls {
    static A h(I b, A a) {
        return b.f(a);
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        T c = new T(a);
        A d = c.g(b);
        A e = h(c, b);
        A f = c.f(a, b);
        Benchmark.test(1, d);
        Benchmark.test(2, e);
        Benchmark.test(3, f);
        Benchmark.test(4, c.p);
    }
}
