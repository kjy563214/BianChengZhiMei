import java.util.Arrays;

/**
 * 1.8 电梯调度问题
 *
 * 现在每次电梯从一层向上走时，我们只允许电梯停在其中某一层，所有蹭课从一楼上电梯，到达某层楼后停下来，
 * 所有乘客再爬楼梯到达自己要去的层。
 *
 * 给定int[] nPerson[] 表示在第i层有多少人要下。
 *
 * 电梯停在哪一楼能保证本次所有乘客爬楼梯成熟之和最小？
 */
public class Q1_8 {
    public static void main(String[] args){
        int[] nPerson = {
                2, 8, 4, 7, 9, 1, 10, 100, 29, 123, 1 , 2, 0 ,1
        };

        Solution1 solution1 = new Solution1(nPerson);
        Solution2 solution2 = new Solution2(nPerson);
    }

    /**
     * 假设停在第k层，我们要找到一个k使得∑(|i-k|*nPerson[i]) (i=0,1...,k)最小
     *
     * 时间复杂度为O(N^2)
     */
    private static class Solution1{

        private int[] nPerson;
        private int maxFloor;

        private int stopFloor = 0;
        private int minTotalFloor = Integer.MAX_VALUE;

        public Solution1(int[] nPerson){
            this.maxFloor = nPerson.length;
            this.nPerson = new int[maxFloor];
            System.arraycopy(nPerson, 0, this.nPerson, 0, maxFloor);

            run();
            print();
        }

        private void run(){

            for (int i=0;i<maxFloor;i++){
                int currentTotalFloor = 0;
                for (int j=0;j<maxFloor;j++){
                    currentTotalFloor += Math.abs(j - i) * nPerson[j];
                }
                if (currentTotalFloor < minTotalFloor){
                    minTotalFloor = currentTotalFloor;
                    stopFloor = i;
                }
            }
        }

        private void print(){
            if (this.stopFloor == 0){
                System.out.println("停在 G 最佳，共需走 " + this.minTotalFloor + " 层");
            }else{
                System.out.println("停在 " + this.stopFloor + " 最佳，共需走 " + this.minTotalFloor + " 层");
            }

        }
    }

    /**
     * 考虑降低时间复杂度。
     * 假设电梯停在 k 层，总共需要爬 Y 层。其中有 N1 个乘客在K层以下下电梯，N2 个乘客在 k 层下电梯，N3 个
     * 乘客在 k 层以上下电梯。
     * 此时如果我们改在 k-1 层停，那么在 k 层以下下电梯的需要少爬 N1 层，k及k层以上的需要多爬 N2+N3 层，总共
     * Y - N1 + N2 + N3 层
     * 同理如果改在 k+1 层停，那么需要爬
     * Y + N1 - N2 - N3 层
     * 所以 Y + N1 + N2 - N3 < Y + N1 - N2 - N3 即 N1 > N2 + N3 层时停在 k-1 层好，N1 + N2 < N3时停在
     * k + 1 层好 相等时 k 层就好
     *
     * 所以从G层开始遍历计算N1 N2 N3并不断调整就可以
     *
     * 时间复杂度为 O(N)
     */
    private static class Solution2{
        private int maxFloor;
        private int[] nPerson;

        private int stopFloor;
        private int minTotalFloor;

        public Solution2(int[] nPerson){
            this.maxFloor = nPerson.length;

            this.nPerson = new int[maxFloor];
            System.arraycopy(nPerson, 0, this.nPerson, 0 ,maxFloor);

            run();
            print();
        }

        private void run(){
            int N1 = 0;
            int N2 = nPerson[0];
            int N3 = 0;

            // 计算0层时N1,N2，N3
            for (int i=1;i<maxFloor;i++){
                N3 += nPerson[i];
                minTotalFloor += nPerson[i] * i;
            }

            // 遍历数组计算N1,N2,N3，调整停的层数
            for (int i=1;i<maxFloor;i++){
                if (N1 + N2 < N3){
                    stopFloor = i;
                    minTotalFloor += N1 + N2 - N3;
                    N1 += N2;
                    N2 = nPerson[i];
                    N3 -= N2;
                }else{
                    break;
                }
            }
        }

        private void print(){
            if (this.stopFloor == 0){
                System.out.println("停在 G 最佳，共需走 " + this.minTotalFloor + " 层");
            }else{
                System.out.println("停在 " + this.stopFloor + " 最佳，共需走 " + this.minTotalFloor + " 层");
            }

        }
    }
}
