package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.J;
import benchmark.objects.K;
import benchmark.objects.L;
import benchmark.objects.M;

public class ClassDispatch {
    static A g(J b, A a) {
        return b.f(a);
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(3);
        A b = new A();
        Benchmark.alloc(4);
        A c = new A();
        Benchmark.alloc(5);
        A d = new A();
        J.p = b;
        L.set(c);
        M.p = d;
        Benchmark.alloc(2);
        J x = new K();
        A y = g(x, a);
        Benchmark.test(1, y);
        Benchmark.test(2, x);
    }
}
