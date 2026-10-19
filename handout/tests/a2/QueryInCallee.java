package a2;
import benchmark.internal.Benchmark;
public class QueryInCallee {
    static int emit(int x) {
        Benchmark.testSign(1, x);
        int y = -x;
        return y;
    }
    static int forward(int x) { return emit(x); }
    static int route(int x) { return forward(x); }
    public static void main(String[] args) {
        int a = route(-3);
        int b = route(0);
        int c = forward(-1);
        Benchmark.testSign(3, a);
        Benchmark.testSign(4, b);
        Benchmark.testSign(5, c);
    }
}
