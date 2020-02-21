/**
 * 2.1 求二进制数中 1 的个数
 *
 * 对于一个字节(8bit)的无符号整型变量，求二进制表示中 1 的个数，要求效率尽可能地高。
 */

/**
 * 1. 各种位操作，一个数除以 2 在二进制上体现，减一在二进制上体现
 * 2. 可以使用空间换时间(Solution 4)
 * 3. 考虑将空间复杂度与总位数相关联变为仅与 1 个数相关联
 */
public class Q2_1 {

    public static void main(String[] args){
        UnsignedByte value = new UnsignedByte((int) Math.floor(Math.random()*256));

        System.out.println("value: " + value.value);

        solution1(value.copy());
        solution2(value.copy());
        solution3(value.copy());
        solution4(value);


    }

    /**
     * 模2取余直至value == 0，这样就可以统计1的个数，实际当一个数除以2时就会减少一位，如果是0就没有余数，
     * 是 1 就会余 1
     * 比如 10100010 / 2 = 1010001 ... 0
     * 1010001 / 2 = 101000 ... 1
     */
    private static void solution1(UnsignedByte value){
        int num = 0;
        while (value.value > 0){
            if (value.value % 2 == 1){
                num ++;
            }
            value.value /= 2;
        }

        System.out.println("Num of 1: " + num);
    }

    /**
     * 位操作，不断向右移位至value为0，过程中与 0x01 与操作，如果得 1 就说明最低位是1
     */
    private static void solution2(UnsignedByte value){
        int num = 0;
        while (value.value != 0){
            num += value.value & 0x01;
            value.value = value.value >>> 1;
        }
        System.out.println("Num of 1: " + num);
    }

    /**
     * 上述两个解法时间复杂度都和value的总位数有关，我们想让复杂度仅与 1 的个数有关。
     * 如果一个数减一，那么这个数从低位向高位起第一个 1 及之前的位都会反向
     * 比如 1000 - 1 = 0111, 0110 - 1 = 0101, 0101 - 1 = 0100
     * 相当于一次从低位消去一个1，所以不断与 value-1 与操作直到为 0 就可以算出 1 的个数
     */
    private static void solution3(UnsignedByte value){
        int num = 0;
        while (value.value != 0){
            value.value &= (value.value - 1);
            num ++;
        }
        System.out.println("Num of 1: " + num);
    }

    /**
     * 跳过数中解法4。
     * 我们事先将 0-255 所有数的 1 个个数计算好放入数组中，输入value时直接取值就可以。
     * 时间复杂度达到 O(1)
     */
    private static void solution4(UnsignedByte value){
        int[] numOf1 = new int[256];
        // 计算 1 的个数，因为我懒得写
        {
            for (int i = 0; i < 256; i++) {
                UnsignedByte v = new UnsignedByte(i);
                int num = 0;
                while (value.value != 0) {
                    value.value &= (value.value - 1);
                    num++;
                }
                numOf1[i] = num;
            }
        }
        System.out.println("Num of 1: " + numOf1[value.value]);
    }

    /**
     * 扩展问题，如果有两个正整数 A B，把 A 变成 B 需要改变几位？
     * 也就是 A 与 B 有几位不相同。
     * 只需要 A ^ B 统计 1 的个数或者 A B 不断右移/除2看向不相同
     */
    private static class AnotherQuestion{

    }
}

class UnsignedByte {

    int value;

    UnsignedByte(int value) {
        if (value < 0 || value > 255){
            System.out.println(value);
            throw new IllegalArgumentException();
        }
        this.value = value & 0xFF;
    }

    UnsignedByte copy(){
        return new UnsignedByte(value);
    }
}
