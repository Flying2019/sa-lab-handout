package a5;

import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.V;

public class FieldFeedback {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        V x = new V(1, 1);
        Benchmark.alloc(2);
        V y = new V(-1, 1);
        Benchmark.alloc(3);
        A a = new A();
        Benchmark.alloc(4);
        A b = new A();
        V z = x;
        if (x.n >= 0) {
            z = y;
        }
        int k = z.m;
        A p = a;
        if (k < 0) {
            p = b;
        }
        Benchmark.testSign(1, k);
        Benchmark.test(2, p);
        Benchmark.test(3, z);
    }
}
