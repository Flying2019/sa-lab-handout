package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class StaticFieldShadow {
    static A p;
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        B e = new B();
        Benchmark.alloc(4);
        B f = new B();
        p = a;
        e.p = b;
        f.p = p;
        Benchmark.test(1, p);
        Benchmark.test(2, e.p);
        Benchmark.test(3, f.p);
    }
}
