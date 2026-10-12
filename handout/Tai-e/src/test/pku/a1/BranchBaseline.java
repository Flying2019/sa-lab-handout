package a1;
import benchmark.internal.Benchmark;
public class BranchBaseline {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int flag=1;
        int x;
        if(flag>2) {
            x=1;
        } else {
            x=-1;
        }
        int y=-x;
        Benchmark.testSign(1,x);
        Benchmark.testSign(2,y);
    }
}
