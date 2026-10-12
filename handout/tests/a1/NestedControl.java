package a1;
import benchmark.internal.Benchmark;
public class NestedControl {
    public static void main(String[] args) {
        int input=args.length-4;
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int i=0;
        int x=0;
        int tail=0;
        while(i<4) {
            i=i+1;
            int j=0;
            while(j<5) {
                j=j+1;
                if(j==1) continue;
                if(input<0) break;
                x=x+ 1;
                if(input==0) continue;
                tail=tail- 1;
            }
            if(input>0) break;
        }
        int same=x-x;
        Benchmark.testSign(1,i);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,tail);
        Benchmark.testSign(4,same);
    }
}
