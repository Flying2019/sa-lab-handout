package demo;
import benchmark.internal.Benchmark;
public class Merge {
    public static void Merge(int input) {
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int flag=input;
        int x;
        if(flag>-3) {
            x=3;
        } else {
            x=0;
        }
        int y=-x;
        Benchmark.testSign(1,x);
        Benchmark.testSign(2,y);
    }
}
/*
隐藏数据更改：
%1 <- [-3, 3]
%2 <- [-3, 3]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 9 行：
        if(flag>%2) {

第 10 行：
            x=%1;
*/
