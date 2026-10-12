package a1;
import benchmark.internal.Benchmark;
public class Loop {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int x=0;
        int i=0;
        int stable=1;
        while(i<input) {
            x=x+ 1;
            i=i+1;
        }
        Benchmark.testSign(1,stable);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,i);
    }
}
