package benchmark.objects;

public class V {
    public int n;
    public int m;
    public A p;
    public V q;

    public V(int n, int m) {
        this.n = n;
        this.m = m;
    }

    public int read() {
        return n;
    }
}
