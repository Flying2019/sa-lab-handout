package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class CallReads {
    static int read(V x) {
        return x.n;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        int i = read(x);
        int j = read(y);
        Benchmark.testSign(1, i);
        Benchmark.testSign(2, j);
    }
}
