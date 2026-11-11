package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;
import benchmark.objects.W;

public class IntegerDispatch {
    static int read(V x) {
        return x.read();
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new W(1, -1);
        int k = read(x);
        Benchmark.testSign(1, k);
        Benchmark.testSign(2, x.n);
        Benchmark.testSign(3, x.m);
    }
}
