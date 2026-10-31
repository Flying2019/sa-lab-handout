package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class BranchJoin {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A d = new A();
        A e;
        if (args.length > 0) {
            e = a;
        }
        else {
            e = b;
        }
        Benchmark.alloc(4);
        B f = new B();
        f.p = e;
        f.q = d;
        Benchmark.test(1, e);
        Benchmark.test(2, f.p);
        Benchmark.test(3, f.q);
    }
}
