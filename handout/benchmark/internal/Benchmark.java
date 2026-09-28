package benchmark.internal;

/** Static-analysis markers. These methods deliberately do not print runtime values. */
public final class Benchmark {
    static final java.util.Set<Integer> visited = new java.util.TreeSet<>();
    private Benchmark() {}
    public static void alloc(int id) {}
    public static void testSign(int id, int value) {}
    public static void test(int id, Object reference) {}
    public static void reach(int id) { visited.add(id); }
}
