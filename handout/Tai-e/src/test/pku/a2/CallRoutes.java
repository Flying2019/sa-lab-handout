package a2;
import benchmark.internal.Benchmark;
public class CallRoutes {
    public static void main(String[] args) {
        int a = CallRoutesLeft.apply(-2);
        int b = CallRoutesLeft.apply(0);
        int c = CallRoutesHelper.compute(3);
        Benchmark.testSign(1, a);
        Benchmark.testSign(2, b);
        Benchmark.testSign(3, c);
    }
}
class CallRoutesLeft {
    static int apply(int x) { return CallRoutesHelper.compute(x); }
}
class CallRoutesHelper {
    static int compute(int x) { return CallRoutesKernel.square(x); }
}
class CallRoutesKernel {
    static int square(int x) { return x * x; }
}
