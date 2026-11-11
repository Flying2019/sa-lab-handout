package a5;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class MultiArray {
    static void put(B[][][] x, A a, int i) {
        x[i][0][0].p = a;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        B y = new B();
        Benchmark.alloc(4);
        B[][][] x = new B[1][1][1];
        y.p = a;
        y.q = b;
        x[0][0][0] = y;
        int i = args.length * 0;
        put(x, b, i);
        Benchmark.test(1, x[0][0][0].p);
        Benchmark.test(2, x[0][0][0].q);
        Benchmark.test(3, x);
        Benchmark.test(4, x[0][0][0]);
    }
}
