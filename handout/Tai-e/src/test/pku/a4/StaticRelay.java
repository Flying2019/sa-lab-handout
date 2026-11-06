package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.G;

public class StaticRelay {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        G x = new G();
        Benchmark.alloc(4);
        G y = new G();
        A c = x.forward(a);
        A d = y.forward(b);
        Benchmark.test(1, c);
        Benchmark.test(2, d);
    }
}
