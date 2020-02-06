import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1.3 烙饼排序问题
 * 对于n块大小不一的烙饼，每次抓住最上边几个颠倒过来，最少要翻多少次能有序？
 */
public class Q1_3 {

    public static void main(String[] args){
        int[] cakeArray = {
                3, 2, 1, 6, 5, 4, 9, 8, 7, 0
        };
        int cakeNum = cakeArray.length;
        Solution1 solution1 = new Solution1(cakeArray, cakeNum);
    }

    /**
     * 未加剪枝算法
     * 讲解 https://blog.csdn.net/fei20121106/article/details/45149683
     * 思路：
     * 先考虑一种方案：
     * 先排好第n个最大的饼，再n-1,n-2...
     * 每次先将最上边的与最大的中间的饼颠倒，再将整个颠倒
     * 如：
     *  -----          ------       ---          -----        --
     *  --             --           -----        ---          ---
     *  ------  -->    -----   -->  --      -->  --      -->  -----
     *  ---            ---          ------       ------       ------
     *  2次可以将一个大饼翻到最底下，所以最多要2(n-1)次（最上边的最后一次直接排好）
     *  接下来使用动态规划来穷举所有翻转方案，利用上述方案作为上限减少翻转次数，
     *  即退出递归条件为次数大于2(n-1)或已经有序
     */
    private static class Solution1{
        private int[] originCakeArray; // 原始烙饼数组
        private int[] cakeArray;  // 用于翻转烙饼数组
        private int cakeNum;  // 烙饼数
        private int maxSwap;  // 最大交换次数
        private int searchTime; // 检索次数计数器

        private int[] swapResult; // 交换结果，记录每次从第几块翻转(i)
        private int[] swapRecord; // 当前烙饼数组，记录每次从第几块翻转(i)

        /**
         * 初始化参量
         * @param cakeArray 烙饼数组
         * @param cakeNum 烙饼数量
         */
        Solution1(int[] cakeArray, int cakeNum){
            // 记录初始数组
            this.originCakeArray = new int[cakeNum];
            System.arraycopy(cakeArray, 0, this.originCakeArray, 0, cakeNum);

            // 初始化用于交换数组
            this.cakeArray = new int[cakeNum];
            System.arraycopy(cakeArray, 0, this.cakeArray, 0, cakeNum);

            this.cakeNum = cakeNum;

            // 记录估计最大可能交换次数，即2(n-1)
            this.maxSwap = getUpperBound(cakeNum);

            this.searchTime = 0;

            // 初始化交换结果数组
            this.swapResult = new int[this.maxSwap + 1];

            // 初始化中间交换数组
            this.swapRecord = new int[this.maxSwap + 1];

            // 执行递归穷举
            run(0);

            // 打印结果
            printResult();
        }

        /**
         * 执行检索
         */
        private void run(int numOfSwap){
            // 搜索次数加1
            searchTime++;

            int lowerBound; // 最小检索次数(下界)
            lowerBound = getLowerBound(this.cakeArray, cakeNum);

            // 如果当前次数加上剩余最小次数都大于最大次数则结束检索
            if (numOfSwap + lowerBound > maxSwap){
                return;
            }

            // 如果已经完成排序则结束检索
            if (isSorted(this.cakeArray, cakeNum)){
                if (numOfSwap < maxSwap){ // 如果本次次数小于当前最大次数则另它为最大次数
                    maxSwap = numOfSwap;
                    // 将本次翻转过程记录下来
                    System.arraycopy(swapRecord, 0, swapResult, 0, maxSwap);
                }
                return;
            }

            // 递归翻转检索
            for (int i=1;i<cakeNum;i++){
                // 从第i个翻转
                reverse(this.cakeArray, 0, i);
                // 记录翻转步骤
                swapRecord[numOfSwap] = i;
                // 递归出子树
                run(numOfSwap + 1);
                // 翻转回来以便从i+1开始翻转
                reverse(this.cakeArray, 0, i);
            }
        }

        /**
         * 翻转给定烙饼从start 到 end
         */
        private void reverse(int[] cakeArray, int start, int end){
            if (end <= start){
                return;
            }

            for (int i=start, j=end;i<j;i++, j--){
                int temp = cakeArray[i];
                cakeArray[i] = cakeArray[j];
                cakeArray[j] = temp;
            }
        }

        /**
         * 获取当前最多翻转次数(上界)
         */
        private int getUpperBound(int numOfCake) {
            return (numOfCake - 1) * 2;
        }

        /**
         * 获取当前最少翻转次数
         */
        private int getLowerBound(int[] cakeArray, int numOfCake){

            int lowerBound = 0;

            for (int i=1;i<numOfCake;i++){
                int delta; // 从第二块饼开始上下两块饼大小差值，0等大，1/-1即大小相邻，否则大小不相邻
                delta = cakeArray[i] - cakeArray[i-1];
                // 判断上下两块饼是否为尺寸相邻
                if (delta == 1 || delta == -1){
                    // 尺寸相邻
                }else{
                    lowerBound++;
                }
            }
            return lowerBound;
        }

        /**
         * 判断给定烙饼堆是否已经排完序
         */
        private boolean isSorted(int[] cakeArray, int numOfCake){
            for (int i=1;i<numOfCake;i++){
                if (cakeArray[i] < cakeArray[i-1]){
                    return false;
                }
            }
            return true;
        }

        /**
         * 输出排序结果
         */
        private void printResult(){
            System.out.println("Search Time: " + searchTime);
            System.out.println("Total swap times: " + maxSwap);

            System.out.println("Original array: " + Arrays.toString(this.originCakeArray));
            System.out.println("Procedure: ");

            // 找到最大的饼
            int[] tmpArray = new int[cakeNum];
            System.arraycopy(this.originCakeArray, 0, tmpArray, 0, cakeNum);
            Arrays.sort(tmpArray);

            int biggestCake = tmpArray[cakeNum - 1];

            // 初始化打印list
            List<StringBuilder> advancedPrintResult = new ArrayList<>();
            for (int i=0;i<this.cakeNum + 1;i++){ // 还要额外加上初始数组
                advancedPrintResult.add(i, new StringBuilder());
            }

            // 将初始数组加到结果里
            int[] swapResult = new int[maxSwap + 1];
            swapResult[0] = 0;
            System.arraycopy(this.swapResult, 0, swapResult, 1, maxSwap);

            // 开始画画
            for (int i=0;i<maxSwap + 1;i++){
                // 先根据记录的翻转顺序翻转
                reverse(this.originCakeArray, 0, swapResult[i]);
                for (int j=0;j<cakeNum;j++){
                    // 画出饼
                    for (int k=0;k<this.originCakeArray[j];k++){
                        advancedPrintResult.get(j).append("-");
                    }
                    // 在中间那层加上-->
                    if (j==this.cakeNum/2){
                        for (int k=0;k<biggestCake - this.originCakeArray[j];k++){
                            advancedPrintResult.get(j).append(" ");
                        }
                        advancedPrintResult.get(j).append("  -->  ");
                    }else{ // 不是中间补空格
                        for (int k=0;k<biggestCake + 7 - this.originCakeArray[j];k++){ // 7是空格加箭头的空
                            advancedPrintResult.get(j).append(" ");
                        }
                    }
                }
            }

            for (int i=0;i<cakeNum;i++){
                System.out.println(advancedPrintResult.get(i));
            }
        }
    }
}
