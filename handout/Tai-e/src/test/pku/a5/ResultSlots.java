package a5;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class ResultSlots {
    static int slot(int n) {
        if (n < 0) {
            return 1;
        }
        return 0;
    }

    static void publish(A[] x, A a, int i) {
        x[i] = a;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A[] x = new A[2];
        int i = slot(args.length);
        publish(x, a, i);
        x[1] = b;
        Benchmark.test(1, x[0]);
        Benchmark.test(2, x[1]);
        Benchmark.testSign(3, i);
    }
}
