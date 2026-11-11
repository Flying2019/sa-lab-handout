package benchmark.objects;

public class N extends D {
    public N() {
    }

    public void set(A[] a) {
        p = a;
    }

    public A get(int i) {
        return p[i];
    }
}
