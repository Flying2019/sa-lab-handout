package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class AliasOverlap {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        B x = new B();
        Benchmark.alloc(2);
        B z = new B();
        Benchmark.alloc(3);
        A a = new A();
        Benchmark.alloc(4);
        A b = new A();
        Benchmark.alloc(5);
        A c = new A();
        B y = x;
        if (args.length > 0) {
            y = z;
        }
        x.p = a;
        z.p = b;
        y.p = c;
        Benchmark.test(1, x);
        Benchmark.test(2, z);
        Benchmark.test(3, y);
        Benchmark.test(4, x.p);
        Benchmark.test(5, z.p);
        Benchmark.test(6, y.p);
    }
}
