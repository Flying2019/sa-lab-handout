package a1;
import benchmark.internal.Benchmark;
public class Operators {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int p=7;
        int n=-8;
        int z=0;
        int neg=-p;
        int mul=p*n;
        int div=p/n;
        int rem=n%p;
        int same=p-p;
        int zero=same*z;
        Benchmark.testSign(1,neg);
        Benchmark.testSign(2,mul);
        Benchmark.testSign(3,div);
        Benchmark.testSign(4,rem);
        Benchmark.testSign(5,same);
        Benchmark.testSign(6,zero);
    }
}
