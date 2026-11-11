package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class NestedOwner {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.alloc(3);
        V z = new V(0, 0);
        z.q = x;
        x.q = y;
        Benchmark.testSign(1, z.q.n);
        Benchmark.testSign(2, z.q.q.n);
        Benchmark.test(3, z.q.q);
    }
}
