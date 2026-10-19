package a2;
import benchmark.internal.Benchmark;
public class StaticReturns {
    static int bridge(int left, int right) { return StaticReturnsHelper.choose(1, left, right); }
    static int route(int left, int right) { return bridge(left, right); }
    public static void main(String[] args) {
        int a = route(-2, -3);
        int b = route(0, 0);
        int c = StaticReturnsHelper.choose(-1, -4, 0);
        int input = args.length - 1;
        int d = StaticReturnsHelper.choose(input, -2, 0);
        Benchmark.testSign(1, a);
        Benchmark.testSign(2, b);
        Benchmark.testSign(3, c);
        Benchmark.testSign(7, d);
    }
}
class StaticReturnsHelper {
    static int choose(int flag, int left, int right) {
        int chosen;
        if (flag > 0) {
            Benchmark.testSign(5, flag);
            chosen = left;
        } else {
            Benchmark.testSign(6, flag);
            if (flag < 0) {
                chosen = right;
            } else {
                Benchmark.testSign(9, flag);
                chosen = right;
            }
        }
        Benchmark.testSign(8, chosen);
        return chosen;
    }
}
