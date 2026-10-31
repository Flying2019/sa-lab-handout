package a3;
import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

public class Fields {
    public static void main(String[] args) {
        Benchmark.alloc(1);
        A b=new A();
        Benchmark.alloc(2);
        A c=new A();
        Benchmark.alloc(3);
        A d=new A();
        Benchmark.alloc(4);
        B x=new B();
        Benchmark.alloc(5);
        B y=new B();
        x.p=b;
        x.p=c;
        x.q=d;
        y.p=d;
        y.q=b;
        Benchmark.test(1,x.p);
        Benchmark.test(2,x.q);
        Benchmark.test(3,y.p);
        Benchmark.test(4,y.q);
    }
}
