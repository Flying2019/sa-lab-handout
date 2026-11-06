package a4;
import benchmark.internal.Benchmark;
import benchmark.objects.A;

public class Identity {
    static A id(A x) {
        return x;
    }

    public static void main(String[] args) {
        Benchmark.alloc(1);
        A a = new A();
        Benchmark.alloc(2);
        A b = new A();
        A x = id(a);
        A y = id(b);
        Benchmark.test(1, x);
        Benchmark.test(2, y);
    }
}
