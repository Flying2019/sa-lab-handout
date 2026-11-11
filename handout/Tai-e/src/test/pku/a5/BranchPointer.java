package a5;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class BranchPointer {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        Benchmark.alloc(3);
        A c = new A();
        Benchmark.alloc(4);
        A d = new A();
        int n = args.length - 1;
        A x;
        int s;
        if (n > 0) {
            Benchmark.testSign(1, n);
            x = a;
            s = 0;
            if (n <= 0) {
                x = d;
            }
        } else {
            Benchmark.testSign(2, n);
            if (n < 0) {
                Benchmark.testSign(3, n);
                x = b;
                s = n;
            } else {
                Benchmark.testSign(4, n);
                x = c;
                s = 0;
                if (n > 0) {
                    x = d;
                }
            }
        }
        Benchmark.test(5, x);
        Benchmark.testSign(6, s);
    }
}
