package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class ReferenceRoute {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.alloc(3);
        V z = new V(0, 0);
        z.q = x;
        V r = z.q;
        Benchmark.testSign(1, r.n);
        Benchmark.test(2, r);
    }
}
