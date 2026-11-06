package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class CallSiteReuse {
    static A outer(B x) {
        return inner(x.p);
    }

    static A inner(A x) {
        Benchmark.test(3, x);
        return x;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        B x = new B();
        Benchmark.alloc(4);
        B y = new B();
        x.p = a;
        y.p = b;
        A c = outer(x);
        A d = outer(y);
        Benchmark.test(1, c);
        Benchmark.test(2, d);
    }
}
