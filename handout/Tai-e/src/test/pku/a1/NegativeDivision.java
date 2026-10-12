package a1;
import benchmark.internal.Benchmark;
public class NegativeDivision {
    public static void main(String[] args) {
        int numerator = -7;
        int divisor = 3;
        int quotient = numerator / divisor;
        int remainder = numerator % divisor;
        int small = -1 / divisor;
        Benchmark.testSign(1, quotient);
        Benchmark.testSign(2, remainder);
        Benchmark.testSign(3, small);
    }
}
