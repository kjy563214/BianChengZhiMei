import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 1.4 买书问题
 * 书店有一套哈利波特，共 5 卷，每一卷 8 欧元，
 * 对于一次购买多本不同卷的，书店有如下促销活动： 本数指基本不同卷
 *  本数    折扣
 *   2       5%
 *   3      10%
 *   4      20%
 *   5      25%
 *   一次购买多本书时，如何能得出最优惠的方案
 */
public class Q1_4 {
    public static void main(String[] args){
        int[] bookList = {2,2,1,1,2};

        Solution2 solution2 = new Solution2(bookList);
    }

    /**
     * 买书方案只需要考虑10本及以下的情况，10本以上可以分解为10及以下的组合
     *
     * 首先考虑贪心算法，假设购买 5 卷书对应的数量为(Y1, Y2, Y3, Y4, Y5)，有Y1>=Y2>=Y3>=Y4>=Y5
     * 那么贪心算法会建议享受如下优惠：
     *    Y5    次 5 本优惠
     *  Y4 - Y5 次 4 本优惠
     *  Y3 - Y4 次 3 本优惠
     *  Y2 - Y3 次 2 本优惠 以及 Y2 - Y1 次不优惠
     * 根据书中列出的枚举表，当购买 8 本时，贪心算法不是最优解，所以考虑把 5+3 都变为4+4的组合，即
     * 把 K 次5本优惠和 K 次3本变为 2*K 次4本优惠(K=min{Y5, Y3-Y4})，如此贪心算法结果为：
     * 买(Y5-K)次 5本优惠和 (Y3-Y4-K)次三本优惠 还有(Y4-Y5+2K)次4本优惠
     * 但最终难以证明贪心算法能得出最优解
     */
    private static class Solution1{

    }

    /**
     * 考虑利用动态规划，
     * 首先考虑书的序号以及本数对应关系是否对结果有影响，即(1,2,3,4,5)和(2,3,5,1,4)等是否可视为同一种情况。
     * 因为每卷书的价格相等，所以每卷书买几本的顺序对结果并没有影响，我们选定
     *    (Y1, Y2, Y3, Y4, Y5)    其中    Y1>=Y2>=Y3>=Y4>=Y5
     * 为某一组合的表示方法，成为“最小表示”
     *
     * 然后考虑如何拆分为小问题，
     * 对于一个集合(Y1, Y2, Y3, Y4, Y5)如果第一次买四本，那么有5中方式
     * (Y1 - 1, Y2 - 1, Y3 - 1, Y4 - 1, Y5)
     * (Y1 - 1, Y2 - 1, Y3 - 1, Y4, Y5 - 1)
     * (Y1 - 1, Y2 - 1, Y3, Y4 - 1, Y5 - 1)
     * (Y1 - 1, Y2, Y3 - 1, Y4 - 1, Y5 - 1)
     * (Y1, Y2 - 1, Y3 - 1, Y4 - 1, Y5 - 1)
     * 那么这五种方式怎么选定可以让接下来的继续分解最优？
     * 假定购买(1,1,1,1,0)得到的(Y1 - 1, Y2 - 1, Y3 - 1, Y4 - 1, Y5)是最优解
     * 那么如果能证明购买(1,1,1,0,1)得到的(Y1 - 1, Y2 - 1, Y3 - 1, Y4, Y5 - 1)也能得到最优解，那么就可以
     * 得出购买的顺序对结果也没有影响。
     * 对于 (Y1 - 1, Y2 - 1, Y3 - 1, Y4, Y5 - 1)，有
     * Y4 > Y5 - 1 (Y4>=Y5) 即第 4 本至少比第 5 本多1本
     * 所以继续拆分(Y1 - 1, Y2 - 1, Y3 - 1, Y4, Y5 - 1)的话每个方案肯定有一个组合中只有第 4 本没有第 5 本
     * 如对于(2,2,2,2,2) 剩下 (1,1,1,2,1) 那么继续拆分
     * 买 {1,2,3,4,5} , {4}*
     * 买 {1,2,3,4}* , {4,5}
     * 买 {1,2,4}* , {3,4,5}
     * ...
     * 带*号只有4而没有5，这样如果把带*号中的4替换为5的话就是(Y1 - 1, Y2 - 1, Y3 - 1, Y4 - 1, Y5)的解
     * 所以购买顺序对结果依然没有影响即(Y1 - 1, Y2 - 1, Y3 - 1, Y4 - 1, Y5)可以代表所有解
     */
    /**
     * 由此得到状态转换方程：
     * F(Y1,Y2,Y3,Y4,Y5) = {
     *     0                                                                   (Y1=Y2=Y3=Y4=Y5=0)
     *     min {
     *         5 * 8 * (1-25%) + F(Y1-1,Y2-1,Y3-1,Y4-1,Y5-1)                   (Y5>=1)
     *         4 * 8 * (1-20%) + F(Y1-1,Y2-1,Y3-1,Y4-1,Y5)                     (Y4>=1)
     *         3 * 8 * (1-10%) + F(Y1-1,Y2-1,Y3-1,Y4,Y5)                       (Y3>=1)
     *         2 * 8 * (1-5%)  + F(Y1-1,Y2-1,Y3,Y4,Y5)                         (Y2>=1)
     *         8 + F(Y1-1,Y2,Y3,Y4,Y5)                                         (Y1>=1)
     *     }
     * }
     */
    private static class Solution2{

        private static final double[] discount = {
                0.05, 0.1, 0.2, 0.25
        };
        private static final int PRICE = 8;
        private static final int TYPE_OF_BOOK = 5;

        private int[] bookList; // 买书清单
        private List<int[]> currentSolution; // 保存递归过程中的拆分方法

        /**
         * @param bookList 形式为 (Y1,Y2,Y3,Y4,Y5)如
         *                 (1,2,3,4,5)表示1本1卷2本2卷...
         */
        public Solution2(int[] bookList){
            // 复制原始清单
            this.bookList = new int[TYPE_OF_BOOK];
            System.arraycopy(bookList, 0, this.bookList, 0, TYPE_OF_BOOK);

            // 将清单化为“最小表示”，即从大到小排序
            sort(this.bookList);

            // 获取最多拆分次数，用于初始化结果数组

            currentSolution = new ArrayList<>();

            float minCost = run(this.bookList);
            System.out.println(minCost);

            System.out.println(currentSolution.size());

            for (int[] i : currentSolution){
                System.out.println(Arrays.toString(i));
            }
        }

        public float run(int[] bookList){
            if (allZero(bookList)){
                return 0;
            }

            float[] conditions = new float[TYPE_OF_BOOK];
            for (int i=0;i<TYPE_OF_BOOK;i++){
                conditions[i] = Float.MAX_VALUE;
            }

            if (bookList[TYPE_OF_BOOK-1] >= 1){
                int[] nextBookList = getNextBookList(bookList, 4);
                conditions[0] = (float) (5*8*(1-0.25) + run(nextBookList));
            }
            if (bookList[TYPE_OF_BOOK-2] >= 1){
                int[] nextBookList = getNextBookList(bookList, 3);
                conditions[1] = (float) (4*8*(1-0.2) + run(nextBookList));
            }
            if (bookList[TYPE_OF_BOOK-3] >= 1){
                int[] nextBookList = getNextBookList(bookList, 2);
                conditions[2] = (float) (3*8*(1-0.1) + run(nextBookList));
            }
            if (bookList[TYPE_OF_BOOK-4] >= 1){
                int[] nextBookList = getNextBookList(bookList, 1);
                conditions[3] = (float) (2*8*(1-0.05) + run(nextBookList));
            }
            if (bookList[TYPE_OF_BOOK-5] >= 1){
                int[] nextBookList = getNextBookList(bookList, 0);
                conditions[4] = (float) (8 + run(nextBookList));
            }

            int index = findMin(conditions);
            currentSolution.add(getNextBookList(bookList, index));

            return conditions[index];
        }

        private int findMin(float[] conditions){
            int index = 0;
            float min = conditions[0];
            for (int i=1;i<conditions.length;i++){
                if (conditions[i] < min){
                    min = conditions[i];
                    index = i;
                }
            }
            return index;
        }

        private int[] getNextBookList(int[] bookList, int index){
            int[] nextBookList = new int[TYPE_OF_BOOK];
            System.arraycopy(bookList, 0, nextBookList, 0, TYPE_OF_BOOK);
            for (int i=0;i<=index;i++){
                nextBookList[i] --;
            }
            sort(nextBookList);
            return nextBookList;
        }

        private boolean allZero(int[] bookList){
            for (int i=0;i<TYPE_OF_BOOK;i++){
                if (bookList[i] != 0){
                    return false;
                }
            }
            return true;
        }

        private void sort(int[] bookList){
            for (int i=0;i<TYPE_OF_BOOK;i++){
                for (int j=i;j<TYPE_OF_BOOK;j++){
                    if (bookList[i] < bookList[j]){
                        bookList[i] = bookList[i] ^ bookList[j];
                        bookList[j] = bookList[i] ^ bookList[j];
                        bookList[i] = bookList[i] ^ bookList[j];
                    }
                }
            }
        }
    }
}
