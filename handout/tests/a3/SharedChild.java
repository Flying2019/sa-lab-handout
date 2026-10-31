package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;
import benchmark.objects.C;

public class SharedChild {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        C e = new C();
        Benchmark.alloc(5);
        C f = new C();
        Benchmark.alloc(6);
        B g = new B();
        Benchmark.alloc(7);
        C h = new C();
        Benchmark.alloc(8);
        h.p = new B();
        h.p.p = c;
        e.p = g;
        f.p = g;
        e.p.p = a;
        f.p.p = b;
        Benchmark.test(1, e.p.p);
        Benchmark.test(2, f.p.p);
        Benchmark.test(3, h.p.p);
    }
}
