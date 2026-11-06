package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class SharedAllocation {
    static B make(A a) {
        Benchmark.alloc(3);
        B x = new B();
        x.p = a;
        return x;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        B x = make(a);
        B y = make(b);
        Benchmark.test(1, x.p);
        Benchmark.test(2, y.p);
        Benchmark.test(3, x);
        Benchmark.test(4, y);
    }
}
