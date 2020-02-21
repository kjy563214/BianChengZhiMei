/**
 * 2.2 不要害怕阶乘
 * 1. 给定一个正整数N,那么N的阶乘N!末尾有多少个0？
 * 2. N!最低位的 1 的位置
 */

/**
 * 1.对于任意一个正整数N，N!可以表示为 N! = (2^A) * (3^B) * (5^C) *...*(k^Z) k为比N小的质数
 * 2.对于任意一个正整数N, N!中可以分解出多少个质因数k可以表示为 num = N/k + N/(k^2) + .... 到k^n>N
 */
public class Q2_2 {
    public static void main(String[] args){
        int N = (int) Math.ceil(Math.random()*255);

        solution1(N);
        solution2(N);

        AnotherQuestion(N);
    }

    /**
     * 0 的个数
     * N!的末尾有几个0说明出现了多少个10。10 = 2*5;所以只需要看比N小的数中因式分解能出几个2*5.又因为2倍数
     * 数量远大于5，所以只需要看有多少个数质数分解能出多少个5.
     * N! = (2^x) * (3^y) * (5^z)。。。也就是各个质数幂的积
     * 所以0的个数 = Min(X,Z)又因为 X 远大于 Z 所以0的个数就是5的个数
     */
    /**
     * 1 的位置
     * 参考Q2_1，一个数除以2就是相当于右移一位，余数就是最低位的值(0或1)。那么对于N!如果对他一直/2那么就相当于
     * 一直除到这个数不是偶数那么就出现了第一个 1，也就是说 N 能分解出多少个 2 也就是说比 N 小的数能分解出
     * 多少个2.
     * 计算方法参考Solution2如何计算 0 的个数
     */
    private static void solution1(int N){
        System.out.println("Solution1: ");

        int num = 0;
        for (int i=5;i<=N;i++){
            int temp = i;
            while (temp % 5 == 0){
                temp /= 5;
                num++;
            }
        }

        System.out.println("Num of 5: " + num);

        num = 0;
        int n = N;
        while (N > 0){
            num += N/2;
            N /= 2;
        }
        System.out.println("Position of first 1: " + num);

        // 等效算法，/2=>>>1
        N = n;
        num = 0;
        while (N != 0){
            N >>>= 1; // N /= 2
            num += N; // num += N/2
        }
        System.out.println("Position of first 1: " + num);
    }

    /**
     * 0 的个数
     * N!分解5的个数 = N/5 + N/(5^2) + ...
     * 理解：对于一个数N, N/5表示他前面出现了多少次5(5的倍数)比如 21/5 = 4(5,10,15,20),这些数都能分解出1个
     * 5，然而对于5的幂比如25能够分解出 2 个5，所以还要统计漏了多少个25，以此类推统计5^3,5^4....
     */
    /**
     * 1 的位置
     * N!中含有质因数2的个数还可以表示为 N - N二进制表示中1的个数。
     */
    private static void solution2(int N){
        System.out.println("\nSolution 2: ");

        int num = 0;
        int n = N;
        while (N > 0){
            num += N/5;
            N /= 5;
        }

        System.out.println("Num of 5: " + num);

        N = n;
        num = 0;
        while (N != 0){
            num += N & 1;
            N >>>= 1;
        }
        num = n - num;

        System.out.println("Position of first 1: " + num);
    }

    /**
     * 给定整数n,判断是否为2的幂
     * 只需要判断是否只含有一个1
     */
    private static void AnotherQuestion(int N){
        System.out.println("\n Is " + N + " a number of 2^k?");

        if (N > 0 && (N & (N - 1)) == 0){ // 参考Q2_1计算1的个数方法Solution3
            System.out.println("Yes, it is");
        }else{
            System.out.println("No, it is not");
        }
    }
}
