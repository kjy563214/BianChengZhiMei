import java.util.*;

/**
 * 1.12 NIM(2) “掂” 游戏分析
 *
 * 有 N 块石头和两个玩家A,B，玩家A先将石头分成若干堆，然后按BABA...的顺序不断轮流取石头，能将剩下的石头一次
 * 取光的玩家获胜，每次取石头时每个玩家只能从若干堆石头中选一堆，取这一堆石头中任意数目(大于0)个。
 *
 * A怎样分配和取石头才能保证自己取胜？
 */
public class Q1_12 {
    public static void main(String[] args) {
        Solution1 solution1 = new Solution1();
    }

    /**
     * 先考虑最基本的情况，用 N 表示有几堆石头，用 M 表示石头总数。我们先放(A)
     *
     * 当 N=1 时，一堆石头 M 为任意值，对手必赢。
     *
     * 如过 M 是偶数
     * 当 N=2 时，两堆石头 M=2 ，如果是 (1,1) 我们必胜，将这种一定获胜的局面称为安全局面。
     * 当 N=2，M>2 时， 易知 (1,X) 都不是安全局面因为 B 只需取 X-1 个石头就会变成对手的安全局面 (1,1)
     *                 再分析基本情况，可知 (2,2) 是安全的，因为它可以一步变成对我们安全的 (2,0) 或者两步
     *                 变成对我们安全的 (1,1)。
     *                 继续分析可知 (3,3) , (4,4) ... (X,X) 都是安全的，它们都可以一部转换成对我们安全的
     *                 (X,0) 或者两步变成 (X-1,X-1) 的安全局面。
     *                 所以，M 为偶数时，只需要分成相等两堆我们必胜
     * 那么再分析 M 是奇数的情况
     * 当 M=3 时，可以分成 (2,1) 或 (1,1,1) 这样对手拿走一个就可以变成对手的安全局面。
     * 当 M=5 时，可以分成 (1,4) (1,3,1) (1,2,1,1) (1,2,2) (1,1,1,1,1) (2,3) 那么对手总可以转变成对于
     *           对于对手安全的 (1,1) 可知 M=5 时对于对手安全
     * 同理 M=7 时，对手总可以转变成安全的 (1,1)
     *
     * 总结一下结论
     * M = X，如果摆成 (1,1,....,1) 时，双方取 X-2 次时 X为奇数就是对于对手安全的 (1,1) 为偶数就是我我们的
     *        如果摆成 (1,1.....,N) 时，如果有奇数个1，B 只需要取走 N-1 个，剩余偶数个1先拿的我们必输
     *                                 如果有偶数个1，B 只需要去走 N 个，剩余偶数个1先拿的我们必输
     *        那么怎么考虑剩下的复杂的摆法呢？
     *
     * 当 M 为偶数时，我们之前的结论是分成两堆相等的，那么
     * 开始时      M1,M1   我方摆石头，两堆相等
     * 1轮        M1,M2   对手取走其中一堆任意数量，使得M1 M2 必不相等
     * 2轮        M2,M2   我方使其变为相等的M2
     * ...
     * X轮        1,0     对手取走任意一个
     * 最后一轮   0,0     我们取走最后一个
     *
     * 我们可以利用异或 XOR 上述过程就变成 XOR(M1,M1) = 0, XOR(M1,M2) != 0, ... XOR (1,0) != 0, XOR(0,0) = 0
     * 即一个 XOR 值在 0,1 间变化的过程，最终将其变为 (0,0) 即 XOR=0 的人获胜
     *
     * 因此分为全为1的任意堆时，XOR(奇数个1) != 0 而 XOR(偶数个1) = 0 因此一方如果能不断将石头数变为 XOR=0
     * 那么就会获胜
     * 我们可以发现当 N 为奇数是，我们不可能将石头分为 XOR = 0 的情况，因为这些堆石头中肯定会有奇数堆有奇数个石头
     * 即 2 进制最低位为1
     * 然后对手只需要改变一堆石头的数量就可以使得总体的 XOR = 0
     * 然后我们再次变动 XOR != 0
     * 最终我们必输
     *
     * 比如 M = 9, 分成 (1,2,6)
     * XOR: 1 = 0 0 1
     *      2 = 0 1 0
     *      6 = 1 1 0
     *      _________
     *    XOR = 1 0 1 != 0
     * 对手取走 6 的 3 个使其变为 (1,2,3)
     *     1 = 0 0 1
     *     2 = 0 1 0
     *     3 = 0 1 1
     *     _________
     *   XOR = 0 0 0 = 0
     *
     *   ...
     *   最终我们输
     */
    private static class Solution1{
        private Scanner scanner;

        private int numOfStone; // Total number of stones
        private int[] divideOfStone; // How user want to divide stones
        private int numOfHeapsOfStone; // Num of heaps of stones

        private int numOfEachRow;
        private int numOfColumn;

        public Solution1(){
            scanner = new Scanner(System.in);

            while (true){
                getNumOfStone();
                getDivideOfStone();
                boolean lose = false;

                while (!isGameOver()){
                    performAction();
                    printBoard();
                    if (isGameOver()){
                        lose = true;
                        System.out.println("You lose..");
                        break;
                    }
                    performUserAction();
                }

                if (!lose){
                    System.out.println("You win!");
                }

                if (!nextRound()){
                    break;
                }
            }
        }

        /**
         * Perform computer player (B) action
         */
        private void performAction(){
            boolean havePerformedAction = false;

            // Try to find action make xor = 0
            for (int i=0;i<numOfHeapsOfStone;i++){

                int xor = 0;

                for (int j=0;j<numOfHeapsOfStone;j++){
                    if (j != i){
                        xor ^= divideOfStone[j];
                    }
                }

                // Perform action and make total xor = 0
                if (xor == 0){ // Take all stones from a heap
                    xor = divideOfStone[i];
                    divideOfStone[i] = 0;
                    havePerformedAction = true;

                    System.out.println("Computer take " + xor + " stones from heap " + (i+1));
                    System.out.println("I think you already lose:)");
                    break;
                }else if(xor < divideOfStone[i]){
                    divideOfStone[i] = xor;
                    havePerformedAction = true;

                    System.out.println("Computer take " + xor + " stones from heap " + (i+1));
                    System.out.println("I think you already lose:)");
                    break;
                }
            }

            // Perform random action if cannot find action make xor = 0
            if (!havePerformedAction){
                int heapIndex = (int) Math.floor(Math.random() * numOfHeapsOfStone);
                int takeNum = (int) Math.ceil(Math.random() * divideOfStone[heapIndex]);

                divideOfStone[heapIndex] -= takeNum;

                System.out.println("Computer take " + takeNum + " stones from heap " + heapIndex+1);
            }
        }

        /**
         * Perform user action (A)
         */
        private void performUserAction(){
            while(true){
                System.out.println("Please your action." +
                        "For example 2,6 as take 2 stones from heap 6");
                try {
                    String[] input = scanner.nextLine().split(",");

                    if (input.length < 2){
                        throw new IllegalArgumentException();
                    }

                    int num = Integer.parseInt(input[0]);
                    int index = Integer.parseInt(input[1]);

                    if (num <= 0 || index <= 0 || index > numOfHeapsOfStone) {
                        throw new IllegalArgumentException();
                    }
                    if (num > divideOfStone[index-1]){
                        System.out.println("Too many stones!");
                        continue;
                    }

                    divideOfStone[index-1] -= num;
                    System.out.println("You take " + num + " stones from heap " + index);
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Invalid input");
                }
            }
        }

        private void getNumOfStone(){
            numOfStone = -1;

            while (true){
                System.out.println("Please input number of stones: ");
                try{
                    numOfStone = Integer.parseInt(scanner.nextLine());

                    if (numOfStone <= 0){
                        throw new IllegalArgumentException();
                    }

                    break;
                }catch (Exception e){
                    System.out.println("Invalid input!");
                }
            }
        }

        private void getDivideOfStone(){
            while (true){
                // Print description
                {
                    System.out.println("Please input how you want to divide the " +
                            numOfStone + " stones? Divide heaps of stones by ,");

                    // Give example of division
                    System.out.print("For example: ");
                    int tempNumOfStone = numOfStone;
                    while (tempNumOfStone > 1) {
                        System.out.print("1,");
                        tempNumOfStone--;
                    }
                    System.out.println("1");
                }

                // Get divide of stones
                try{
                    List<Integer> tempDivideOfStone = new ArrayList<>();
                    String[] userDivision = scanner.nextLine().split(",");

                    int countOfDivisions = 0;
                    for (String divide : userDivision){
                        int numOfDivide = Integer.parseInt(divide);

                        if (numOfDivide <= 0){
                            throw new IllegalArgumentException();
                        }

                        countOfDivisions += numOfDivide;

                        tempDivideOfStone.add(numOfDivide);
                    }

                    if (countOfDivisions != numOfStone){
                        System.out.println("Wrong number of total stones.");
                        continue;
                    }

                    divideOfStone = tempDivideOfStone.stream().mapToInt(Integer::valueOf).toArray();
                    numOfHeapsOfStone = divideOfStone.length;
                    break;
                }catch (Exception e){
                    System.out.println("Invalid divide.");
                }
            }
        }

        private void printBoard(){
            System.out.println("\n--------------------------\n");
            if (numOfEachRow == 0){
                // Make average stones number as each row of stones
                numOfEachRow = numOfStone / numOfHeapsOfStone;
            }

            if (numOfColumn == 0){
                // Calculate column of stones
                int[] tempHeapsStones = new int[numOfHeapsOfStone];
                System.arraycopy(divideOfStone, 0, tempHeapsStones, 0, numOfHeapsOfStone);
                Arrays.sort(tempHeapsStones);

                numOfColumn = tempHeapsStones[numOfHeapsOfStone-1]/numOfEachRow;
            }
            List<StringBuilder> printBuilder = new ArrayList<>();

            // Set up indexes;
            StringBuilder indexes = new StringBuilder();
            for (int i=0;i<numOfHeapsOfStone;i++){
                for (int j=0;j<numOfEachRow/2;j++){
                    indexes.append(" ");
                }
                String index = (i+1)+":"+divideOfStone[i];
                indexes.append(index);
                if (numOfEachRow%2==0){
                    for (int j=0;j<numOfEachRow/2+4-index.length();j++){
                        indexes.append(" ");
                    }
                }else{
                    for (int j=0;j<numOfEachRow/2+4-index.length()+1;j++){
                        indexes.append(" ");
                    }
                }
            }
            printBuilder.add(indexes);

            int[] tempHeapsStones = new int[numOfHeapsOfStone];
            System.arraycopy(divideOfStone, 0, tempHeapsStones, 0, numOfHeapsOfStone);
            // Set up Stones
            for (int i=0;i<numOfColumn;i++){
                StringBuilder line = new StringBuilder();
                for (int j=0;j<numOfHeapsOfStone;j++){
                    if (tempHeapsStones[j] - numOfEachRow >=0){
                        for (int k=0;k<numOfEachRow;k++){
                            line.append("o");
                        }
                        tempHeapsStones[j] -= numOfEachRow;
                    }else{
                        for (int k=0;k<tempHeapsStones[j];k++){
                            line.append("o");
                        }
                        for (int k=0;k<numOfEachRow-tempHeapsStones[j];k++){
                            line.append(" ");
                        }
                        tempHeapsStones[j] = 0;
                    }
                    line.append("    ");
                }
                printBuilder.add(line);
            }

            Collections.reverse(printBuilder);
            for (StringBuilder line : printBuilder){
                System.out.println(line);
            }

            System.out.println("\n--------------------------\n");
        }

        private boolean isGameOver(){
            for (int i=0;i<numOfHeapsOfStone;i++){
                if (divideOfStone[i] != 0){
                    return false;
                }
            }
            return true;
        }

        private boolean nextRound(){
            while (true){
                System.out.println("Want to play another round? Y/N");
                String input = scanner.nextLine();
                if (input.toUpperCase().equals("Y")) {
                    return true;
                }else if (input.toUpperCase().equals("N")){
                    return false;
                }
            }
        }
    }

    /**
     * 如果最后取光所有石头的人输怎么办？
     */
    private static class Question2{

    }
}
