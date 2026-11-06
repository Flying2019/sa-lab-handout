package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.G;

public class SameReceiver {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        G x = new G();
        A c = x.get(a);
        A d = x.get(b);
        Benchmark.test(1, c);
        Benchmark.test(2, d);
        G e = x.self();
        Benchmark.test(4, e);
    }
}
