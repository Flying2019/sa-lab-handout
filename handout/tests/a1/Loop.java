package demo;
import benchmark.internal.Benchmark;
public class Loop {
    public static void Loop(int input) {
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int x=0;
        int i=0;
        int stable=1;
        while(i<input) {
            x=x+ 1;
            i=i+1;
        }
        Benchmark.testSign(1,stable);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,i);
    }
}
/*
隐藏数据更改：
%1 <- [-3, 3]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 9 行：
        int stable=%1;

第 11 行：
            x=x+ %1;
*/
