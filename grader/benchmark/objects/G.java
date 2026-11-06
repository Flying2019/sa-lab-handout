package benchmark.objects;

public class G {
    public A p;
    public A q;

    public G() {
    }

    public G(A a, A b) {
        p = a;
        q = b;
    }

    public A get() {
        return p;
    }

    public A get(A a) {
        return a;
    }

    public G self() {
        return this;
    }

    public void put(A a) {
        E.p = a;
    }

    public A read() {
        return E.p;
    }

    public static A relay(A a) {
        return a;
    }

    public A forward(A a) {
        return relay(a);
    }
}
