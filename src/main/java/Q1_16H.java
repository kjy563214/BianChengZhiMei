import java.util.*;

/**
 * 1.16 24点游戏
 * 给玩家4张牌，牌值在 1-13 (A-K) 之间，可以有相同值的牌。玩家采用加减乘除，允许运算过程存在小数，并且可以
 * 使用括号，但每张牌只能用一次，尝试构造一个表达式使得运算结果为24.
 */

/**
 * 1. 如何生成数组全排列
 */
public class Q1_16H {
    public static void main(String[] args){
        //Solution1 solution1 = new Solution1();
        Solution2 solution2 = new Solution2();
    }

    /**
     * 枚举法，枚举 4 个数字所有可能的运算方案。
     * 首先考虑数字的顺序，共有 A44 = 4！ = 24 种排列方式。
     * 然后是运算符号，4个数字有 3 个空位放 4 个运算符，共有 4*4*4 = 64 种方式。
     * 然后是括号，对于 A B C D 括号可以：
     * (((AB)C)D) ((AB)(CD)) ((A(BC))D) (A((BC)D) (A(B(CD)))
     * 共 5 种
     * 所以一共是 24 * 64 * 5 = 7680 种方案
     */
    private static class Solution1 {

        private final double THRESHOLD = 1 * (Math.pow(10, -6)); // 用来避免double误差

        private int[] cards;

        private double[] numbers;
        String[] result;
        List<String> solutions;

        public Solution1() {
            generate4Numbers();
            printNumbers();

            numbers = new double[4];
            for (int i=0;i<4;i++){
                numbers[i] = cards[i];
            }

            result = new String[4];
            for (int i=0;i<4;i++){
                result[i] = numbers[i]+"";
            }
            solutions = new ArrayList<>();
            generateAnswer(4);

            if (solutions.size() == 0){
                System.out.println("There is no solution");
            }else {
                for (String s : solutions){
                    System.out.println(s);
                }
            }

        }

        private void generate4Numbers() {
            cards = new int[4];

            List<Integer> cards = new ArrayList<>(); // 模拟扑克牌
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 14; j++) {
                    cards.add(j);
                }
            }

            for (int i = 0; i < 4; i++) {
                int index = (int) Math.floor(Math.random() * cards.size());
                this.cards[i] = cards.get(index);
                cards.remove(index);
            }
        }

        private void printNumbers() {
            System.out.println("Your cards:");
            for (int i = 0; i < 4; i++) {
                switch (cards[i]) {
                    case 1:
                        System.out.print("A ");
                        break;
                    case 11:
                        System.out.print("J ");
                        break;
                    case 12:
                        System.out.print("Q ");
                        break;
                    case 13:
                        System.out.print("K ");
                        break;
                    default:
                        System.out.print(cards[i] + " ");
                }
            }
            System.out.println();
        }

        private void generateAnswer(int n) {
            // 终止条件
            if (n == 1 && Math.abs(numbers[0] - 24) < THRESHOLD){ // 如果与24的误差不超过一个极小值，避免double的计算误差
                solutions.add(result[0]);
            }

            for (int i=0;i<n;i++){
                for (int j=i+1;j<n;j++){ // 从数组中取 2 个数
                    double a = numbers[i];
                    double b = numbers[j];
                    numbers[j] = numbers[n - 1]; // 相当于删除numbers[j]并留下n-1

                    String expa = result[i];
                    String expb = result[j];
                    result[j] = result[n-1]; // 相当于删除 result[j]并留下n-1

                    result[i] = "(" + expa + '+' + expb + ')';
                    numbers[i] = a + b; // 记录运算结果
                    generateAnswer(n-1); // 递归计算子数组所有组合

                    result[i] = "(" + expa + '-' + expb + ')';
                    numbers[i] = a - b;
                    generateAnswer(n-1);

                    result[i] = "(" + expb + '-' + expa + ')';
                    numbers[i] = b - a;
                    generateAnswer(n-1);

                    result[i] = "(" + expa + '*' + expb + ')';
                    numbers[i] = a * b;
                    generateAnswer(n-1);

                    if (b != 0){
                        result[i] = "(" + expa + '/' + expb + ')';
                        numbers[i] = a / b;
                        generateAnswer(n-1);
                    }

                    if (a != 0){
                        result[i] = "(" + expa + '/' + expb + ')';
                        numbers[i] = b / a;
                        generateAnswer(n-1);
                    }

                    numbers[i] = a;
                    numbers[j] = b;
                    result[i] = expa;
                    result[j] = expb; // 还原两个数进行下一个两个数的组合
                }
            }
        }
    }

    /**
     * 运用分治法进行剪枝并缩小时间复杂度。
     * 将数组分成两个子数组，分别计算子数组所有计算结果并计算两子数组结果的所有可能计算结果，最终得出初始数组
     * 的所有结果。
     *
     * 对于子集，我们可以用一个4位二进制数 a 来表示，从右往左 1 表示含有该元素，0 表示不含有，比如对于
     * A{a1, a2, a3, a4} a=0011 表示 {a1, a2}，a=1000 表示 {a4}
     * 那么 15-a (1111 - a) 就是 该子集对于全集 A 的补集
     */
    private static class Solution2 {
        private final double THRESHOLD = 1 / (Math.pow(10, -6));

        private int[] cards;
        private double[] numbers;
        //Map<List<Double>, Double> results;
        List<List<Double>> results;

        public Solution2(){
            generate4Numbers();
            printNumbers();

            numbers = new double[4];
            for (int i=0;i<4;i++){
                numbers[i] = cards[i];
            }

            generateAnswers(numbers);
        }

        private void generate4Numbers() {
            cards = new int[4];

            List<Integer> cards = new ArrayList<>(); // 模拟扑克牌
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 14; j++) {
                    cards.add(j);
                }
            }

            for (int i = 0; i < 4; i++) {
                int index = (int) Math.floor(Math.random() * cards.size());
                this.cards[i] = cards.get(index);
                cards.remove(index);
            }
        }

        private void printNumbers() {
            System.out.println("Your cards:");
            for (int i = 0; i < 4; i++) {
                switch (cards[i]) {
                    case 1:
                        System.out.print("A ");
                        break;
                    case 11:
                        System.out.print("J ");
                        break;
                    case 12:
                        System.out.print("Q ");
                        break;
                    case 13:
                        System.out.print("K ");
                        break;
                    default:
                        System.out.print(cards[i] + " ");
                }
            }
            System.out.println();
        }

        private void generateAnswers(double[] array){
            results = new LinkedList<>();
            for (int i=0;i<=(int) Math.pow(2, 4) - 1;i++){
                results.add(new ArrayList<>());
            }

            for (int i=0;i<4;i++){
                int index = (int) Math.pow(2, i);
                results.get(index).add(array[i]);
            }

            for (int i=1;i<=(int) Math.pow(2, 4) - 1;i++){
                results.set(i, calculateSubArrays(i));
            }

            for (Double value : results.get((int) Math.pow(2, 4) - 1)){
                if (Math.abs(value - 24) < THRESHOLD){
                    System.out.println(results.get((int) Math.pow(2, 4) - 1));
                }
            }
        }

        private List<Double> calculateSubArrays(int i){
            if (results.get(i).size() > 0){
                return results.get(i);
            }

            for (int x = 1; x < i;x++){
                if ((x & i) == x){
                    System.out.println(x + " " + i);
                    results.get(i).addAll(fork(calculateSubArrays(x), calculateSubArrays(i-x)));
                }
            }

            return results.get(i);
        }

        private List<Double> fork(List<Double> array1, List<Double> array2){

            List<Double> result = new ArrayList<>();

            for (Double a : array1) {
                for (Double b : array2) {

                    result.add(a + b);
                    result.add(a - b);
                    result.add(b - a);
                    result.add(a * b);

                    if (a != 0) result.add(b / a);
                    if (b != 0) result.add(a / b);
                }
            }

            return result;
        }
    }

    /**
     * 生成全排列
     */
    private void generateNumberCombinations(int[] nums, int start, int end, List<int[]> combinations) {
        if (start == end) { // 进行到最后一个数，将结果加入list
            int[] newNums = new int[4];
            if (end >= 0) System.arraycopy(nums, 0, newNums, 0, 4);
            combinations.add(newNums);
        } else {
            for (int i = start; i <= end; i++) { // 交换剩下的 start 到 end 的数
                int temp = nums[start]; // 逐个交换第一与第i个
                nums[start] = nums[i];
                nums[i] = temp;

                generateNumberCombinations(nums, start + 1, end, combinations); // 递归交换剩下的数
                nums[i] = nums[start];

                nums[i] = nums[start]; // 还原数组，进行下一个数的交换
                nums[start] = temp;
            }
        }
    }
}
