package a1;
import benchmark.internal.Benchmark;
public class Arithmetic {
    public static void main(String[] args) {
        int input=args.length-4;
        int x=6;
        int y=x*7;
        int z=y-50;
        Benchmark.testSign(1,y);
        Benchmark.testSign(2,z);
    }
}
