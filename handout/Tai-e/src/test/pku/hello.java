package pku;

public class hello {
    public static int compute(int input) {
        int a = 10;
        int b = 3;
        int c = a + b;
        int d = c * 2;
        return input + d;
    }

    public static void main(String[] args) {
        int result = compute(0);
    }
}
