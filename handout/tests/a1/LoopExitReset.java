package a1;
import benchmark.internal.Benchmark;
public class LoopExitReset {
    public static void main(String[] args) {
        int rounds = args.length;
        if (rounds > 3) rounds = 3;
        int value = -1;
        int i = 0;
        while (i < rounds) {
            value = -value;
            i = i + 1;
            if (i == 2) break;
        }
        value = 0;
        Benchmark.testSign(1, value);
        Benchmark.testSign(2, i);
    }
}
