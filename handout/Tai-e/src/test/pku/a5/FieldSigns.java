package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class FieldSigns {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.testSign(1, x.n);
        Benchmark.testSign(2, y.n);
        Benchmark.testSign(3, x.m);
    }
}
