package demo;
import benchmark.internal.Benchmark;
public class Operators {
    public static void Operators(int input) {
        if(input < -4) input = -4;
        if(input > 4) input = 4;
        int p=7;
        int n=-8;
        int z=0;
        int neg=-p;
        int mul=p*n;
        int div=p/n;
        int rem=n%p;
        int same=p-p;
        int zero=same*z;
        Benchmark.testSign(1,neg);
        Benchmark.testSign(2,mul);
        Benchmark.testSign(3,div);
        Benchmark.testSign(4,rem);
        Benchmark.testSign(5,same);
        Benchmark.testSign(6,zero);
    }
}
/*
隐藏数据更改：
%1 <- [-9, 9]
%2 <- [-9, 9]
%3 <- [-9, 9]
同一占位符的所有出现位置使用相同取值；不同占位符可同时改变。
行号以本文件注释前的程序为准。

第 7 行：
        int p=%1;

第 8 行：
        int n=%2;

第 9 行：
        int z=%3;

约束：%1 != 0，%2 != 0。

第 10 ~ 15 行：
        顺序可能变化；same 的定义必须先于 zero。
        六条定义的其余顺序任意。

第 16 ~ 21 行：
        查询变量随第 10 ~ 15 行的定义次序排列，编号依次为 1 ~ 6。
*/
