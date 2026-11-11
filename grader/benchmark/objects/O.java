package benchmark.objects;

public class O extends H {
    public O() {
    }

    public static void link(H x, H y) {
        x.p = y;
    }

    public A get() {
        return r;
    }
}
