package demo;
import benchmark.internal.Benchmark;
public class SixSigns {
    public static void SixSigns(int input) {
        int p=3;
        int n=-2;
        int z=0;
        int nonnegative;
        int nonpositive;
        int any;
        if(input>0) {
            nonnegative=p;
            nonpositive=n;
            any=p;
        } else {
            nonnegative=z;
            nonpositive=z;
            any=n;
        }
        Benchmark.testSign(1,p);
        Benchmark.testSign(2,n);
        Benchmark.testSign(3,z);
        Benchmark.testSign(4,nonnegative);
        Benchmark.testSign(5,nonpositive);
        Benchmark.testSign(6,any);
    }
}
/*
隐藏数据更改：
%1 <- [-3, 3]
%2 <- [-3, 3]
%3 <- [-3, 3]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 5 行：
        int p=%1;

第 6 行：
        int n=%2;

第 7 行：
        int z=%3;
*/
