package a2;
import benchmark.internal.Benchmark;
public class VoidSideQuery {
    static void visit(int x) {
        if (x <= 0) return;
        Benchmark.testSign(1, x);
    }
    static int mirror(int x) { return -x; }
    public static void main(String[] args) {
        visit(0);
        visit(-3);
        visit(4);
        int y = mirror(-2);
        Benchmark.testSign(2, y);
    }
}
