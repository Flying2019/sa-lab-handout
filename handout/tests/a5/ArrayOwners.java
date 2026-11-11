package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.V;

public class ArrayOwners {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 0);
        Benchmark.alloc(2);
        V y = new V(-1, 0);
        Benchmark.alloc(3);
        V[] r = new V[2];
        r[0] = x;
        r[1] = y;
        int i = args.length * 0;
        V z = r[i];
        Benchmark.testSign(1, z.n);
        Benchmark.testSign(2, r[1].n);
        Benchmark.test(3, z);
    }
}
