import java.util.*;

/**
 * 1.13 NIM(3) 两堆石头的游戏
 *
 * 现在有两堆石头，有两个玩家轮流取石头，没人每次可以从一堆石头中取任意数量的石头，或者从两堆石头里取数量相等
 * 的石头，最后把剩下石头一次拿光的人获胜。
 *
 * 给定函数 boolean nim(n,m); //n,m 表示两堆石头的数量
 * 要求返回值表示先取的人能否获胜？
 */
public class Q1_13 {

    private static int n;
    private static int m;
    private static Map<Integer, Integer> unsafe;

    public static void main(String[] args){
        getNM();
        Solution1 solution1 = new Solution1(n, m);
        Solution2 solution2 = new Solution2(n, m);
        Solution3 solution3 = new Solution3(n, m);

        unsafe = solution3.getUnsafe();

        System.out.println(unsafe);

        if (!canWin()){
            System.out.println("I think you already lose:)");
        }

        while (!isGameOver()){
            printBoard();
            performUserAction();
            if (isGameOver()){
                System.out.println("You win!");
                return;
            }
            performAction();
        }
        printBoard();
        System.out.println("You lose..");
    }

    /**
     * 列表考虑各种情况，带括号的表示安全局面
     *  n 1     2     3     4     5     6     7     8     9
     * m
     * 1 (1,1)  1,2  (1,3) (1,4) (1,5) (1,6) (1,7) (1,8) (1,9)
     * 2       (2,2) (2,3) (2,4) (2,5) (2,6) (2,7) (2,8) (2,9)
     * 3             (3,3) (3,4)  3,5  (3,6) (3,7) (3,8) (3,9)
     * 4                   (4,4) (4,5) (4,6)  4,7  (4,8) (4,9)
     * 5                         (5,5) (5,6) (5,7) (5,8) (5,9)
     * 6                               (6,6) (6,7) (6,8) (6,9)
     * 7                                     (7,7) (7,8) (7,9)
     * 8                                           (8,8) (8,9)
     * 9                                                 (9,9)
     *
     * 首先我们知道 n=m 时肯定是安全局面，先手可以一次拿走，
     * n != m 时，最基本的(1,2)不是安全局面，我们只需要考虑剩下的情况先手能否将状况转换成对于对手的不安全局面
     * (1,m) 都是安全局面，因为先手可以一次取走 m-2 个转换成对手的 (1,2) 不安全局面
     * (2,m) 都是安全局面，因为可以取走 m-1 个变成 (1,2)
     * (3,m) 时 (3,4) 可以同时取 2,2 变成 (1,2) 然而我们发现 (3,5) 无法变成 (1,2)因此非安全，剩下的 3 开头
     * 都可以一次变成 (3,5) 因此都是安全的
     * 往后同理
     *
     * 所以我们对于给定的 n,m 只需要计算不安全局面 (x,y) 到 x>=n 并判断是否有
     *  n==x && m==y
     * 就可以知道此 (n,m) 是否是非安全局面
     *
     * 对于一组不安全局面(xn,yn)
     * 已知 x1 = 1, y1 = 2
     * 如果已知 x1,y1,x2,y2,...,xn-1,yn-1那么 xn 未出现在这 2(n-1) 个数中的下一个整数
     * 并且 yn = xn + n
     *
     * xn 是因为比如 (1,2) 能够保证(1,m)跟(2,m) m > 2都是安全局面，因为先手一次就能转变成对手的(1,2)，所以
     * 对于任意(m,n) m>n 如果 m 在数列 xn,yn..中出现过，那么他肯定安全
     * yn 是因为看上边的表对于第 n 个 x, (x,x) (x,x+1) (x,x+2) 都可已转换成之前的(1,2) (3,5)。。。
     * 只有(x,x+n)没有对应的之前的情况，而(x,x+n+1)。。。又可以转换成(x,x+n)
     */
    private static class Solution1{

        private Map<Integer, Integer> unsafe; // 不安全局面记录，一个x对应一个y

        Solution1(int n, int m){
            calculateUnsafeConditions(n, m);
        }

        Map<Integer, Integer> getUnsafe(){
            return unsafe;
        }

        private void calculateUnsafeConditions(int n, int m){
            // 保证n>m
            if (n > m) {
                n = n ^ m;
                m = n ^ m;
                n = n ^ m;
            }
            int x = 1; // 不安全局面推理第一堆
            int y = 2; // 不安全局面推理第二堆

            List<Integer> unsafeList = new ArrayList<>();
            unsafe = new HashMap<>();
            // 添加第一种条件(1,2)
            unsafe.put(x, y);
            unsafeList.add(y);

            int delta = 1;

            while (x < n){
                // 找到下一个 xn
                while (unsafeList.indexOf(++x) != -1);
                delta ++;
                unsafeList.add(x+delta);
                unsafe.put(x+delta-unsafeList.indexOf(x+delta)-1, x+delta);
            }
        }
    }

    /**
     * 笨方法，枚举遍历所有可能性判断是否安全
     */
    private static class Solution2{

        private Map<Integer, Integer> unsafe; // 不安全局面记录，一个x对应一个y

        public Solution2(int n, int m){
            calculateUnsafeConditions(n, m);
        }

        public Map<Integer, Integer> getUnsafe(){
            return unsafe;
        }

        private void calculateUnsafeConditions(int n, int m){
            if (n > m){
                n = n ^ m;
                m = n ^ m;
                n = n ^ m;
            }

            unsafe = new HashMap<Integer, Integer>(){{
                this.put(1, 2);
            }};


            for (int x=2;x<=n;x++){
                for (int y=x+1;y<=m;y++){
                    boolean safe = false;
                    for (int firstHeap : unsafe.keySet()){
                        if (firstHeap > x){
                            break;
                        }
                        // 尝试构造之前的不安全局面
                        // 尝试拿y或都拿
                        int takeNum = x - firstHeap;
                        if (firstHeap < x && unsafe.get(firstHeap) == y){ // 只取x就可以构造,比如(4,5)
                            safe = true;
                            break;
                        }

                        if (unsafe.get(firstHeap) == y-takeNum){ // 两堆取相同的可以构造
                            safe = true;
                            break;
                        }

                        // 尝试以y为依据
                        takeNum = y - firstHeap;
                        if (firstHeap < y && unsafe.get(firstHeap) == x){ // 只取y就可以,取完y小，比如(5,4)
                            safe = true;
                            break;
                        }

                        if (firstHeap == x && unsafe.get(x) < y){ // 只取y就可以,取完y大,比如(3,7)
                            safe = true;
                            break;
                        }

                        if (unsafe.get(firstHeap) == x-takeNum){ // 两堆取相同的可以构造
                            safe = true;
                            break;
                        }
                    }

                    // 无法构造
                    if (!safe){
                        unsafe.put(x, y);
                    }
                }
            }
        }
    }

    /**
     * 我们能发现an, bn 数组规律：他们是所有正整数的集合，并且 bn-an = n
     * 得出通项公式：
     * an = ceil(a*n), bn = ceil(b*n)
     * a = (1+sqrt(5)) / 2 = 1.7
     * b = (3+sqrt(5)) / 2 = 2.7
     */
    private static class Solution3{

        private Map<Integer, Integer> unsafe;

        public Solution3(int n, int m){
            unsafe = new HashMap<Integer, Integer>(){{
                this.put(1,2);
            }};

            calculateUnsafeConditions(n, m);
        }

        private void calculateUnsafeConditions(int n, int m){
            int index = 1;

            final double a = (1 + Math.sqrt(5)) / 2;
            final double b = (3 + Math.sqrt(5)) / 2;

            while (Math.ceil(a*index) < n){
                int an = (int) Math.floor(a*index);
                int bn = (int) Math.floor(b*index);

                unsafe.put(an, bn);
                index ++;
            }
        }

        public Map<Integer, Integer> getUnsafe() {
            return unsafe;
        }
    }

    private static boolean canWin(){
        // 相等一定安全
        if (n == m){
            return true;
        }

        int nt = Math.min(n, m);
        int mt = Math.max(n, m);

        return unsafe.get(nt) == null || (unsafe.get(nt) != mt);
    }

    private static void performAction(){
        int nt = n;
        int mt = m;

        boolean reversed = false;
        if (nt > mt){
            nt = nt ^ mt;
            mt = nt ^ mt;
            nt = nt ^ mt;
            reversed = true;
        }

        // 能赢
        if (n == 0){ // 取光第二堆
            System.out.println("Computer take " + m + " from heap 2");
            m = 0;
            return;
        }

        if (m == 0){ // 取光第一堆
            System.out.println("Computer take " + n + " from heap 1");
            n = 0;
            return;
        }

        if (n == m){ // 都取光
            System.out.println("Computer take " + n + " from heap 1 and 2");
            n = 0;
            m = 0;
            return;
        }

        // 尝试构造必赢局面
        if (!canWin()){
            for (int x: unsafe.keySet()){
                int takeNum = nt-x;
                if (unsafe.get(x) == mt){ // 只从小堆取就可以必胜 (4,5) -> (3,5)
                    if (reversed){
                        System.out.println("Computer take " + takeNum + " from heap 2");
                        m -= takeNum;
                        return;
                    }else{
                        System.out.println("Computer take " + takeNum + " from heap 1");
                        n -= takeNum;
                        return;
                    }
                }

                takeNum = mt-x;
                if (x == nt && mt > unsafe.get(x)){ // 只从大堆取就能赢 (3,7) -> (3,5)
                    if (reversed){
                        System.out.println("Computer take " + takeNum + " from heap 1");
                        n -= takeNum;
                        return;
                    }else{
                        System.out.println("Computer take " + takeNum + " from heap 2");
                        m -= takeNum;
                        return;
                    }
                }

                // 取两堆 (4,6) -> (3,5)
                takeNum = nt - x;
                if (unsafe.get(x) == mt-takeNum){
                    System.out.println("Computer take " + takeNum + " from heap 1 and 2");
                    n -= takeNum;
                    m -= takeNum;
                    return;
                }
            }
        }


        // 没找到能赢得取法，随机取
        System.out.println("Umm... I dont't know how to win yet");
        int from1 = (int) Math.ceil(Math.random()); // =0从1堆取，=1从两堆取

        if (from1 == 0){
            int fromHeap = (int) Math.ceil(Math.random()); // =0从n取，=1从m取

            if (fromHeap == 0){
                int takeNum = (int) Math.floor(Math.random() + nt-1);
                System.out.println("Computer take " + takeNum + " from heap 1");
                n -= takeNum;
            }else{
                int takeNum = (int) Math.floor(Math.random() + mt-1);
                System.out.println("Computer take " + takeNum + " from heap 2");
                m -= takeNum;
            }
        }else{
            int min = Math.min(n, m);
            int takeNum = (int) Math.floor(Math.random() + min-1);
            System.out.println("Computer take " + takeNum + " from heap 1 and 2");
            n -= takeNum;
            m -= takeNum;
        }
    }

    private static void performUserAction(){
        while (true){
            System.out.println("Please input how you want to take the stones. Divide by ,\n" +
                    "For example 1,0 as take 1 stone from heap 1 or 2,1 as take 2 stones" +
                    "from heap 1 and 1 from 2nd");

            Scanner scanner = new Scanner(System.in);

            try{
                String[] takes = scanner.nextLine().split(",");

                if (takes.length < 2){
                    throw new IllegalArgumentException();
                }

                int take1 = Integer.parseInt(takes[0]);
                int take2 = Integer.parseInt(takes[1]);

                if (take1 ==0 && take2 == 0){
                    throw new IllegalArgumentException();
                }

                if (take1 < 0 || take1 > n){
                    System.out.println("You cannot do that to heap 1");
                }

                if (take2 < 0 || take2 > m){
                    System.out.println("You cannot do that to heap 2");
                }

                n -= take1;
                m -= take2;
                break;
            }catch (Exception e){
                System.out.println("Invalid input!");
            }
        }
    }

    private static boolean isGameOver(){
        if (n == 0 && m == 0){
            return true;
        }

        return false;
    }

    private static void printBoard(){
        int eachRow = n>m? n/3 : m/3;
        eachRow = Math.max(eachRow, 1);
        int numColumn = n>m? (int) Math.ceil(n/eachRow) : (int) Math.ceil(m/eachRow);

        int nt = n;
        int mt = m;

        List<StringBuilder> printBuilder = new ArrayList<>();

        StringBuilder indexes = new StringBuilder();
        String index1 = 1 + ":" + nt;
        String index2 = 2 + ":" + m;
        for (int i=0;i<2*(eachRow+4) - index1.length()+index2.length();i++){
            indexes.append(" ");
        }
        indexes.insert(eachRow/2, index1);
        indexes.insert(eachRow/2 + eachRow+4, index2);

        printBuilder.add(indexes);

        // Build board
        for (int i=0;i<numColumn;i++){
            StringBuilder line = new StringBuilder();
            if (nt >= eachRow){
                for (int j=0;j<eachRow;j++){
                    line.append("o");
                }
                nt -= eachRow;
            }else{
                for (int j=0;j<nt;j++){
                    line.append("o");
                }
                for (int j=0;j<eachRow-nt;j++){
                    line.append(" ");
                }
                nt = 0;
            }
            line.append("    ");
            if (mt >= eachRow){
                for (int j=0;j<eachRow;j++){
                    line.append("o");
                }
                mt -= eachRow;
            }else{
                for (int j=0;j<mt;j++){
                    line.append("o");
                }
                for (int j=0;j<eachRow-mt;j++){
                    line.append(" ");
                }
                mt = 0;
            }

            printBuilder.add(line);
        }

        Collections.reverse(printBuilder);

        System.out.println("------------------------------");
        for (StringBuilder line : printBuilder){
            System.out.println(line);
        }
        System.out.println("------------------------------");
    }

    private static void getNM(){
        while (true){
            System.out.println("Please input number of 2 heaps of stones, divide by ,\n" +
                    "For example: 1,2");

            Scanner scanner = new Scanner(System.in);

            try {
                String[] nm = scanner.nextLine().split(",");

                if (nm.length < 2) throw new IllegalArgumentException();

                n = Integer.parseInt(nm[0]);
                m = Integer.parseInt(nm[1]);

                if (n <= 0){
                    System.out.println("Invalid first heap");
                    continue;
                }

                if (m <= 0){
                    System.out.println("Invalid second heap");
                    continue;
                }
                break;
            }catch (Exception e){
                System.out.println("Invalid input");
            }
        }
    }
}
