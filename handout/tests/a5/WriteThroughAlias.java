package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class WriteThroughAlias {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        V z = x;
        z.m = -1;
        y.m = 1;
        Benchmark.testSign(1, x.m);
        Benchmark.testSign(2, y.m);
        Benchmark.test(3, z);
    }
}
