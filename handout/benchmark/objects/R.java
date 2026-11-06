package benchmark.objects;

public class R {
    public A p;

    public R(A a) {
        p = a;
    }

    public A f(A a) {
        return p;
    }

    public A f(A a, A b) {
        return b;
    }
}
