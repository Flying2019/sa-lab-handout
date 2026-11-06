package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class ArrayRelay {
    static void put(A[] x, A a, A b) {
        x[0] = a;
        x[1] = b;
        Benchmark.test(5, a);
    }

    static A[] relay(A[] x) {
        return x;
    }

    static A first(A[] x) {
        return x[0];
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        A d = new A();
        Benchmark.alloc(5);
        A[] x = new A[2];
        Benchmark.alloc(6);
        A[] y = new A[2];
        put(x, a, b);
        put(y, c, d);
        A[] z = relay(x);
        A[] w = relay(y);
        A e = first(z);
        A f = first(w);
        Benchmark.test(1, e);
        Benchmark.test(2, f);
        Benchmark.test(3, z);
        Benchmark.test(4, w);
    }
}
