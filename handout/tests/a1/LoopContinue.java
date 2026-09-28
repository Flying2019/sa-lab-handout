package demo;
import benchmark.internal.Benchmark;
public class LoopContinue {
    public static void LoopContinue(int input) {
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int i=0;
        int x=0;
        while(i<3) {
            i=i+1;
            if(input<0) continue;
            x=x- 2;
        }
        int same=x-x;
        Benchmark.testSign(1,i);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,same);
    }
}
/*
隐藏数据更改：
%1 <- [-3, 3]
%2 <- [2, 4]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 9 行：
        while(i<%2) {

第 12 行：
            x=x- %1;
*/
