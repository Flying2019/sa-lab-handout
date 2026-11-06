package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class FieldCarry {
    static void put(B x, A a) {
        x.p = a;
        Benchmark.test(5, a);
    }

    static A get(B x) {
        return x.p;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        B x = new B();
        Benchmark.alloc(5);
        B y = new B();
        x.p = a;
        y.p = b;
        x.q = b;
        y.q = a;
        B z = x;
        if (args.length > 0) {
            z = y;
        }
        put(z, c);
        put(y, b);
        A d = get(x);
        A e = get(y);
        Benchmark.test(1, d);
        Benchmark.test(2, e);
        Benchmark.test(3, x.q);
        Benchmark.test(4, y.q);
        Benchmark.test(6, z);
        Benchmark.test(7, z.p);
    }
}
