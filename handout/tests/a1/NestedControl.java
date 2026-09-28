package demo;
import benchmark.internal.Benchmark;
public class NestedControl {
    public static void NestedControl(int input) {
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int i=0;
        int x=0;
        int tail=0;
        while(i<4) {
            i=i+1;
            int j=0;
            while(j<5) {
                j=j+1;
                if(j==1) continue;
                if(input<0) break;
                x=x+ 1;
                if(input==0) continue;
                tail=tail- 1;
            }
            if(input>0) break;
        }
        int same=x-x;
        Benchmark.testSign(1,i);
        Benchmark.testSign(2,x);
        Benchmark.testSign(3,tail);
        Benchmark.testSign(4,same);
    }
}
/*
隐藏数据更改：
%1 <- [-3, 3]
%2 <- [2, 4]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 10 行：
        while(i<%2) {

第 13 行：
            while(j<%2+1) {

第 17 行：
                x=x+ %1;

第 19 行：
                tail=tail- %1;
*/
