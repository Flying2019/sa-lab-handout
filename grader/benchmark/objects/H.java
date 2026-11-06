package benchmark.objects;

public class H {
    public H p;
    public H q;
    public A r;

    public H() {
    }

    public A get() {
        return p.q.p.r;
    }
}
