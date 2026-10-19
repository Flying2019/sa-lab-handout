package a2;
import benchmark.internal.Benchmark;
public class Recursive {
    static int f(int n) {
        if(n<=0) return 3;
        return f(n-1);
    }
    public static void main(String[] args) {
        int x=f(3);
        Benchmark.testSign(1,x);
    }
}
