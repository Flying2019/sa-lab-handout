package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.V;

public class AliasBranch {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.alloc(3);
        A a = new A();
        Benchmark.alloc(4);
        A b = new A();
        V z = x;
        int k = z.n;
        A p = a;
        if (k < 0) {
            p = b;
        }
        Benchmark.test(1, p);
        Benchmark.testSign(2, k);
    }
}
