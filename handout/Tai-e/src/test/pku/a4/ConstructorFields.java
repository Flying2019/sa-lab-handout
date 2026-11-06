package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.G;

public class ConstructorFields {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        A d = new A();
        Benchmark.alloc(5);
        G x = new G(a, b);
        Benchmark.alloc(6);
        G y = new G(c, d);
        A e = x.get();
        A f = y.get();
        Benchmark.test(1, e);
        Benchmark.test(2, x.q);
        Benchmark.test(3, f);
        Benchmark.test(4, y.q);
    }
}
