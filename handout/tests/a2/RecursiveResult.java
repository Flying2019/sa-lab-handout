package a2;
import benchmark.internal.Benchmark;
public class RecursiveResult {
    static int count(int n) {
        if (n <= 0) return 0;
        int left = count(n - 1);
        int right = count(n - 1);
        return 1 + left + right;
    }
    public static void main(String[] args) {
        int empty = count(0);
        int tree = count(3);
        Benchmark.testSign(1, empty);
        Benchmark.testSign(2, tree);
    }
}
