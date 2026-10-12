package a1;
import benchmark.internal.Benchmark;
public class Merge {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int flag=input;
        int x;
        if(flag>-3) {
            x=3;
        } else {
            x=0;
        }
        int y=-x;
        Benchmark.testSign(1,x);
        Benchmark.testSign(2,y);
    }
}
