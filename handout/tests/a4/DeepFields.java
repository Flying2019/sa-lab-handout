package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.H;

public class DeepFields {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        H x = new H();
        Benchmark.alloc(2);
        H y = new H();
        Benchmark.alloc(3);
        H z = new H();
        Benchmark.alloc(4);
        H w = new H();
        Benchmark.alloc(5);
        A a = new A();
        Benchmark.alloc(6);
        A b = new A();
        x.p = y;
        x.q = z;
        y.q = z;
        z.p = w;
        z.q = y;
        w.p = x;
        w.q = x;
        y.r = b;
        w.r = a;
        A c = x.get();
        A d = z.get();
        Benchmark.test(1, c);
        Benchmark.test(2, d);
        Benchmark.test(3, x.q.q.r);
    }
}
