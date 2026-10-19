package a2;
import benchmark.internal.Benchmark;
public class MultiFile {
    public static void main(String[] args) {
        int result=MultiFileHelper.select(-2,0,3);
        Benchmark.testSign(1,result);
    }
}
class MultiFileHelper {
    static int select(int first,int second,int third) {
        return MultiFileBridge.forward(third) * first + second;
    }
}
class MultiFileBridge {
    static int forward(int x) { return -MultiFileLeaf.negate(x); }
}
class MultiFileLeaf {
    static int negate(int x) { return -x; }
}
