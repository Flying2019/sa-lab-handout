package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.E;
import benchmark.objects.F;
import benchmark.objects.G;

public class StaticFields {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        G x = new G();
        Benchmark.alloc(5);
        G y = new G();
        x.p = a;
        y.p = c;
        F.p = a;
        x.put(a);
        y.put(b);
        A d = x.read();
        A e = y.read();
        Benchmark.test(1, d);
        Benchmark.test(2, e);
        Benchmark.test(3, E.p);
        Benchmark.test(4, F.p);
        Benchmark.test(5, x.p);
        Benchmark.test(6, y.p);
    }
}
