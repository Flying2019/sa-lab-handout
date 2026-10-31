package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class StaticFields {
    static A d;
    static A e;
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        d = a;
        e = c;
        d = b;
        Benchmark.test(1, d);
        Benchmark.test(2, e);
    }
}
