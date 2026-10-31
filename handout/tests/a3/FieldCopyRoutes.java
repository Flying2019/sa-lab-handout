package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class FieldCopyRoutes {
    static B[] d;
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        B f = new B();
        Benchmark.alloc(5);
        B g = new B();
        Benchmark.alloc(6);
        B h = new B();
        Benchmark.alloc(7);
        B i = new B();
        f.p = a;
        f.q = c;
        h.q = f.q;
        g.p = b;
        h.p = f.p;
        i.p = g.p;
        f.p = b;
        Benchmark.alloc(8);
        d = new B[2];
        d[0] = h;
        d[1] = i;
        Benchmark.test(13, d[0].p);
        Benchmark.test(4, d[1].p);
        Benchmark.test(27, h.q);
        Benchmark.test(9, f.q);
    }
}
