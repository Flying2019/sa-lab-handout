package a2;
import benchmark.internal.Benchmark;
public class VariableJoin {
    static int classify(int x, int zero) {
        if (x < zero) {
            Benchmark.testSign(1, x);
            return x;
        }
        if (x > zero) {
            Benchmark.testSign(2, x);
            return -x;
        }
        Benchmark.testSign(3, x);
        return x;
    }
    public static void main(String[] args) {
        int result = 0;
        int value = args.length - 1;
        int round = 0;
        while (round < 2) {
            result = classify(value, 0);
            value = -value;
            round = round + 1;
        }
        Benchmark.testSign(4, result);
    }
}
