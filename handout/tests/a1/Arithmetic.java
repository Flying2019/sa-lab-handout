package demo;
import benchmark.internal.Benchmark;
public class Arithmetic {
    public static void Arithmetic(int input) {
        int x=6;
        int y=x*7;
        int z=y-50;
        Benchmark.testSign(1,y);
        Benchmark.testSign(2,z);
    }
}
/*
隐藏数据更改：
%1 <- [-6, 6]
%2 <- [-7, 7]
%3 <- [-50, 50]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 5 行：
        int x=%1;

第 6 行：
        int y=x*%2;

第 7 行：
        int z=y-(%3);
*/
