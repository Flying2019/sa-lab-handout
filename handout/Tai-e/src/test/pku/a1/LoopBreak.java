package a1;
import benchmark.internal.Benchmark;
public class LoopBreak {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int i=0;
        int x=0;
        while(i<3) {
            i=i+1;
            if(input<0) break;
            x=x+ 2;
        }
        int same=x-x;
        Benchmark.testSign(1,i);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,same);
    }
}
