package benchmark.objects;

public class S extends R implements I {
    public S(A a) {
        super(a);
    }

    public A f(A a) {
        return a;
    }

    public A g(A a) {
        return super.f(a);
    }
}
