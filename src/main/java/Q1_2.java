/**
 * 中国象棋将帅问题。
 * 对于中国象棋棋盘的将与帅，有多少种不会相对的情况？
 *
 * 仅用1byte实现
 *
 * 技巧1： 对于m*n棋盘上某一点k，坐标为(k/n + 1, k%n)
 * 技巧2： 本题byte操作
 */
public class Q1_2 {
    static byte container;
    public static void main(String[] args){
        //Solution1();
        Solution2();
    }

    /**
     * 经典解法，用一个byte左四位表示黑子，右四位表示红字坐标。通过byte操作判断情况
     * 思路:
     * 将与帅的位置可视为：
     * 1 - 2 - 3              (1,1) - (1,2) - (1,3)
     * |   |   |                |       |       |
     * 4 - 5 - 6  或坐标形式   (2,1) - (2,2) - (2,3)
     * |   |   |                |       |       |
     * 7 - 8 - 9              (3,1) - (3,2) - (3,3)
     * 当两棋子纵坐标不同时不相对
     */
    private static void Solution1(){
        //Solution1();
        for (setLeft(1);getLeft() <= 9;setLeft(getLeft() + 1)){
            for (setRight(1);getRight() <= 9;setRight(getRight() + 1)){
                if (getLeft() % 3 != getRight() % 3){
                    System.out.println("Black: " + getLeft() + " Red: " + getRight());
                }
            }
        }
    }

    /**
     * 将一共91种解法视为一个9*9的新矩阵: 其中每一个解法组合正好是该点坐标
     * (1,1) (1,2) ... (1.9)
     * (2,1) (2,2) ... (2,9)
     *   .     .         .
     *   .     .         .
     *   .     .         .
     * (9,1) (9,2) ... (9,9)
     * 对于第i个解法，该组合(坐标)即为(i/9+1, i%9+1),
     * 则红黑棋子纵坐标为(i/9+1) % 3 与 (i%9 +1) % 3，
     * 所以i / 9 % 3 != i % 9 % 3两棋子不会相对
     */
    private static void Solution2(){
        byte i = (byte) 0;
        while (i++ < 81){
            if (i / 9 % 3 != i % 9 % 3){
                System.out.println("Black: " + (i / 9 + 1) + " Red: " + (i % 9 + 1));
            }
        }
    }

    private static byte getLeft(){
        return (byte) ((container & 0xf0) // 清空右半部分
                >> 4); // 左移4位获得结果
    }

    private static byte getRight(){
        return (byte) (container & 0x0f); // 清空右半部分
    }

    private static void setLeft(int target){
        container = (byte) ((container & 0x0f) // 清空左半部分
                | (target << 4)); // 将目标数左移4位，与container取或获得结果
    }

    private static void setRight(int target){
        container = (byte) ((container & 0xf0) // 清空右半部分
                        | (target)); // 与目标取或获得结果
    }
}
