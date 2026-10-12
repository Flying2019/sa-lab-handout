package a1;
import benchmark.internal.Benchmark;
public class OperatorsReverse {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int p=7;
        int n=-8;
        int z=0;
        int mul=p*n;
        int same=p-p;
        int neg=-p;
        int zero=same*z;
        int div=p/n;
        int rem=n%p;
        Benchmark.testSign(1,mul);
        Benchmark.testSign(2,same);
        Benchmark.testSign(3,neg);
        Benchmark.testSign(4,zero);
        Benchmark.testSign(5,div);
        Benchmark.testSign(6,rem);
    }
}
