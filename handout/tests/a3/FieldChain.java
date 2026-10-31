package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;
import benchmark.objects.C;

public class FieldChain {
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
        e.p = new B();
        Benchmark.alloc(7);
        f.p = new B();
        e.p.p = a;
        e.p.q = b;
        f.p.p = b;
        B j = e.p;
        j.p = c;
        Benchmark.alloc(8);
        B k = new B();
        k.p = c;
        k.q = b;
        e.p = k;
        Benchmark.test(1, e.p.p);
        Benchmark.test(2, f.p.p);
        Benchmark.test(3, k.p);
        Benchmark.test(4, e.p.q);
    }
}
