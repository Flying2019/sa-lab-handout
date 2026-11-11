package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.V;

public class LateIntegerWrite {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.alloc(3);
        A a = new A();
        Benchmark.alloc(4);
        A b = new A();
        int k = x.n;
        A p = a;
        if (k < 0) {
            p = b;
        }
        if (args.length > 0) {
            x.n = -1;
        }
        Benchmark.testSign(1, k);
        Benchmark.test(2, p);
        Benchmark.testSign(3, y.n);
    }
}
