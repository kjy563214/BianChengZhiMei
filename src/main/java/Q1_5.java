import java.util.HashMap;
import java.util.Map;

/**
 * 1.5 快速找出故障机器
 * 现在有很多计算机来保存信息，为了保证服务质量，每份数据都有备份。
 * 假设一个机器仅储存一个标号为 ID 的记录 ( Id < 1 * 10^9 ) 假设每个数据保存两份备份，即有两个机器保存了
 * 同样的数据
 * 1. 在某个时间如果得到一个数据文件 ID 的列表，能否快速找出这个列表中仅出现一次的ID? 即坏机器
 * 2. 如果已经知道有一台机器死机(即只有一个备份丢失)呢？如果有 2 台机器死机(假设同一个数据的两份备份不会
 *    同时丢失)呢？
 */
public class Q1_5 {
    public static void main(String[] args){
    }

    /**
     * 遍历列表记录ID出现次数输出为1的,可用数组代替map
     * 时间复杂度O(N), 空间复杂度O(N)
     */
    private static class Solution1{

        Map<Integer, Integer> records = new HashMap<>();

        public Solution1(int[] ids){
            for (int i=0;i<ids.length;i++){
                if (records.get(ids[i]) == null){
                    records.put(ids[i], 1);
                }else{
                    records.put(ids[i], records.get(ids[i])+1);
                }
            }

            System.out.println("Solution1: ");
            for (Integer i : records.keySet()){
                if (records.get(i) == 1){
                    System.out.println(i + " appears only once.");
                }
            }
        }
    }

    /**
     * Solution1 的改进，如果Id出现次数为2就去掉
     * 时间复杂度O(N), 空间复杂度最好O(1), 最坏O(N)
     */
    private static class Solution2{
        private Map<Integer, Integer> records;

        public Solution2(int[] ids){
            for (int i=0;i<ids.length;i++){
                if (records.get(ids[i]) == null){
                    records.put(ids[i], 1);
                }else{
                    if (records.get(ids[i]) == 2){
                        records.remove(ids[i]);
                    }else{
                        records.put(ids[i], records.get(ids[i]) + 1);
                    }
                }
            }

            System.out.println("Solution 2");
            for (Integer i : records.keySet()){
                System.out.println(i + " appears only once.");
            }
        }
    }

    /**
     * 进一步优化空间复杂度，考虑寻找一个函数 f(list[0], list[1], ... , list[n])作为唯一变量，也就是说
     * 遍历完整个表后变量值为该唯一id。
     * 考虑使用异或，因为 X ⊕ X = 0 并且 X ⊕ 0 = X，并且异或满足交换律与结合律，所以如果仅有一个坏机器，
     * list[0] ⊕ list[1] ⊕ ... ⊕ list[n] 就是那个唯一id，出现两次的id 异或为0
     *
     * 如果有多个坏机器即有多个ID出现不止一次，假设两台机器为A,B，那么 xor = A ⊕ B，分情况讨论：
     * 1. 如果A = B, 即A⊕B = 0, 那么A = B = (丢失前id和 - 丢失后id和) / 2，需要知道丢失前id值
     * 2. 如果A != B，即A⊕B ！= 0，那么结果值xor中某一位为1的话，A,B中有且只有一个数这个位上为1，那么将
     *    数组分为两类，该位上位1和不为1，分别取异或结果就是A，B
     *
     * 时间复杂度O(N), 空间复杂度O(1)
     */
    private static class Solution3{

        int xor;

        public Solution3(int[] ids){
            xor = 0;

            handleOnlyOneBroken(ids);
            handleMoreThanOne(ids, 0);
        }

        private void handleOnlyOneBroken(int[] ids){
            for (int i=0;i<ids.length;i++){
                xor = xor ^ ids[i];
            }

            System.out.println("Solution 3, not consider multiple broken: ");
            System.out.println(xor + " appears only once");
        }

        private void handleMoreThanOne(int[] ids, long originSum){
            xor = 0;
            long sum = 0;

            for (int i=0;i<ids.length;i++){
                xor = xor ^ ids[i];
                sum += ids[i];
            }

            System.out.println("Solution 3, not consider multiple broken: ");

            if (xor == 0){
                System.out.println("A = B = " + (sum - originSum)/2 );
            }else{
                int indexOf1 = indexOf1(xor);

                int A = 0; // 含1
                int B = 0; // 不含1

                for (int i=0;i<ids.length;i++){
                    if (toBinary(ids[i]).charAt(indexOf1) == '1'){
                        A ^= ids[i];
                    }else{
                        B ^= ids[i];
                    }
                }
                System.out.println("A != B, A = " + A + " B = " + B);
            }
        }

        private int indexOf1(int num){
            String numBinary = toBinary(num);
            for (int i=0;i<numBinary.length();i++){
                if (numBinary.charAt(i) == '1'){
                    return i;
                }
            }

            return -1;
        }

        private String toBinary(int num){
            StringBuilder binary = new StringBuilder();
            for (int i=0;i<32;i++){
                binary.append((xor & 0x80000000 >>> i) >>> (31-i)); // >>>无符号右移即移完高位直接补0,
                                                                    // (xor & 0x80000000 >>> i)算出来
                                                                    // 第i位的值再右移(31-i)位即到最右边，
                                                                    // 加到stringbuilder里
            }
            return binary.toString();
        }
    }

    /**
     * Solution 3 对于A = B时，必须知道丢失数据前的和否则无法得出AB，
     * 如果我们预先计算好id的和，oSum，可以只利用和解决问题
     * 1.只有一个时，用 oSum - Sum 即是该id
     * 2.不止一个时，oSum - Sum = A + B = x，我们需要另一个方程联立求解，可以计算原始乘积 oMultiple,
     *   oMultiple / Multiple即是 A*B，联立可得A, B:
     *   A + b = x
     *     AB  = y
     *   因为id值较大，乘积很可能会数据溢出，可以用 list[0]^2 + list[0]^2 +...得出 A^2 + B^2联立求解
     */
    private static class Solution4{

    }
}
