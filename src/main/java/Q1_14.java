import java.util.*;
import java.util.function.Predicate;

/**
 * 1.14 连连看游戏设计
 * 对于一个连连看游戏，规则简单描述就是
 * 如果用户可以用线(拐弯不能多于两个)将两个图片连起来，那么这两个图片就会消掉，全消掉就取得胜利。
 * 假如让你设计一个连连看游戏，你会怎么设计？请说明：
 * 1. 怎么样用简单的计算机模型来描述这个问题？
 * 2. 怎么样判断两个图片能否相消？
 * 3. 怎么样求出相同图形间的最短路径？(转弯数最少，经过格子最少)
 * 4. 怎么样确定死锁状态？又如何设计算法解除死锁？
 */

/**
 * 1. 如何随机范围内不重复全部数字的一种方法
 * 2. 最短路径检索
 * 3. 如何在此基础上加入转弯的算法
 * 4. 如何费尽心思打印出很丑的棋盘
 */
public class Q1_14 {
    public static void main(String[] args){
        Solution1 solution1 = new Solution1();
    }

    /**
     * 采用正方形棋盘
     * 1. 判断能否相消
     *   为了简单解法不采用图形，只用纯命令行界面，因此只需要判断数字是否相同就行
     * 2. 关于如何找最短路径
     *   本质为经典寻找最短路径问题，不过连连看需要考虑转弯次数
     * 3. 死锁问题
     *   可以遍历一次棋盘找到有无死锁情况，或者一直保存每两个相同图案的最短路径，每次消去就更新数据
     * 4. 游戏模型参考构造函数结构
     */
    public static class Solution1{

        private static final char[] CHAR_LIST = { // 会出现的图案
                '@', '#', '$', '%', '&',
                '(', 'A', 'B', 'C', 'D',
                'E', 'F', 'G', 'H', 'I',
                'K', 'L', 'M', 'N', 'O',
                'P', 'R', 'S', 'T', 'U',
                'W', 'X', 'Y', 'Z', '?'
        };
        private static final int MAX_SCALE = 8; // 最大棋盘
        private static final int MIN_SCALE = 3; // 最小棋盘

        char[][] board; // 棋盘数组
        final int scale; // 用户选的大小
        final int numOfCell; // 有多少个格子就是 scale^2
        private List<Cell> path; // 用于打印
        private Map<Character, List<Cell>> pairs; // 用于记录成对图案

        public Solution1(){
            this.scale = getScaleOfBoard();

            numOfCell = scale*scale;
            board = new char[scale+2][scale+2];
            pairs = new HashMap<>();

            generateGameBoard();

            while (!isGameFinished()){
                printBoard();
                Cell[] cells = getUserSelection();
                Cell[] remove = new Cell[2];
                int count = 0;
                if (canEliminate(cells[0], cells[1])){ // 移除消除的
                    for (Cell c : pairs.get(board[cells[0].x][cells[0].y])){
                        if ((c.x == cells[0].x && c.y == cells[0].y) ||
                                (c.x == cells[1].x && c.y == cells[1].y)){
                            remove[count++] = c;
                        }
                        if (count == 2) break;
                    }
                    pairs.get(board[cells[0].x][cells[0].y]).remove(remove[0]);
                    pairs.get(board[cells[0].x][cells[0].y]).remove(remove[1]);

                    board[cells[0].x][cells[0].y] = ' ';
                    board[cells[1].x][cells[1].y] = ' ';
                }

                if (!canPlay()){
                    System.out.println("Regenerating Board...");
                    do {
                        reGenerateBoard();
                    }while (!canPlay());
                }

            }
        }

        private int getScaleOfBoard(){
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("Please input the length/width of the game board (even number" +
                        "less than " + MAX_SCALE + " and larger than " + MIN_SCALE + "):");

                try{
                    int scale = Integer.parseInt(scanner.nextLine());

                    if (scale < MIN_SCALE){
                        System.out.print("That is too small. ");
                        throw new IllegalArgumentException();
                    }

                    if (scale > MAX_SCALE){
                        System.out.print("That is too big. ");
                        throw new IllegalArgumentException();
                    }

                    if (scale % 2 != 0){
                        System.out.print("Please input even number. ");
                        throw new IllegalArgumentException();
                    }

                    return scale;
                }catch (Exception e){
                    System.out.println("Invalid input");
                }
            }
        }

        private void generateGameBoard(){
            for (int i=0;i<scale+2;i++){
                for (int j=0;j<scale+2;j++){
                    board[i][j] = ' ';
                }
            }

            // 首先生成scale^2 / 3个charlist中随机字符
            char[] chars = generateCharList();

            // 从随机好的chars中取图案填充gameBoard
            List<List<Integer>> positionRemaining = new LinkedList<>();
            for (int i=1;i<=scale;i++){
                List<Integer> column = new LinkedList<>();
                for (int j=1;j<=scale;j++){
                    column.add(j);
                }
                positionRemaining.add(column);
            }

            int counter = scale * scale;
            int cIndex = 0;

            int fillNUm = scale * scale / 2 / chars.length;

            while (counter > 0){
                char c = chars[cIndex];

                List<Cell> cells = new ArrayList<>();
                pairs.put(c, cells);
                if (cIndex < chars.length-1){ // 前面的图案每次填充filNum组
                    for (int i=0;i<fillNUm;i++){
                        fillBoard(c, positionRemaining);
                        counter -= 2;
                    }
                }else{ // 最后一组填充满剩下的
                    for (int i=0;counter > 0;i++) {
                        fillBoard(c, positionRemaining);
                        counter -= 2;
                    }
                }

                cIndex ++;
            }
        }

        private char[] generateCharList(){
            int numOfPattern = numOfCell / 3;
            char[] chars = new char[numOfPattern];
            {
                int counter = 0;
                while (counter < numOfPattern) {
                    int index = (int) Math.floor(Math.random() * CHAR_LIST.length);
                    char c = CHAR_LIST[index];
                    boolean repeat = false;
                    for (int i = 0; i < counter; i++) {
                        if (chars[i] == c) {
                            repeat = true;
                            break;
                        }
                    }
                    if (!repeat) {
                        chars[counter] = c;
                        counter++;
                    }
                }
            }

            return chars;
        }

        private void fillBoard(char c, List<List<Integer>> positionRemaining) {
            for (int i = 0; i < 2; i++) { // 每次随机两个位置填充
                int y;
                do {
                    y = (int) Math.floor(Math.random() * positionRemaining.size());
                } while (positionRemaining.get(y).size() == 0);

                int xIndex = (int) Math.floor(Math.random() * positionRemaining.get(y).size());
                int x = positionRemaining.get(y).get(xIndex);

                board[x][y+1] = c;

                pairs.get(c).add(new Cell(x, y+1));

                positionRemaining.get(y).remove(xIndex);
            }
        }

        private boolean isGameFinished(){
            for (int i=1;i<scale;i++){
                for (int j=1;j<scale;j++){
                    if (board[i][j] != ' '){
                        return false;
                    }
                }
            }
            return true;
        }

        private void printBoard(){
            System.out.print("   ");
            for (int i=1;i<scale+1;i++){
                System.out.print(i);
            }
            System.out.println("  ");
            for (int i=0;i<scale+2;i++){
                if (i!=0 && i!=scale+1) System.out.print(i + " ");
                System.out.println(board[i]);
            }
            System.out.print("   ");
            for (int i=1;i<scale+1;i++){
                System.out.print(i);
            }
            System.out.println("  ");
        }

        private Cell[] getUserSelection(){
            Cell[] cells = new Cell[2];

            while (true){
                System.out.println("Please select 2 cells to be eliminated, in form of x1,y1 x2,y2\n" +
                        "For example: 1,1 2,2");
                Scanner scanner = new Scanner(System.in);

                try{
                    String[] input = scanner.nextLine().split(" ");

                    if (input.length < 2){
                        throw new IllegalArgumentException();
                    }

                    String[] cell1String = input[0].split(",");
                    String[] cell2String = input[1].split(",");

                    if (cell1String.length < 2 || cell2String.length < 2){
                        throw new IllegalArgumentException();
                    }

                    Cell cell1 = new Cell(Integer.parseInt(cell1String[1]),
                            Integer.parseInt(cell1String[0]));

                    Cell cell2 = new Cell(Integer.parseInt(cell2String[1]),
                            Integer.parseInt(cell2String[0]));

                    if (cell1.getPattern() == ' '){
                        System.out.print(cell1 + " is empty. ");
                        throw new IllegalArgumentException();
                    }

                    if (cell2.getPattern() == ' '){
                        System.out.print(cell2 + " is empty. ");
                        throw new IllegalArgumentException();
                    }

                    cells[0] = cell1;
                    cells[1] = cell2;

                    break;
                }catch (Exception e){
                    System.out.println("Invalid input");
                }
            }

            return cells;
        }

        private boolean canEliminate(Cell cell1, Cell cell2){
            // 首先看两个图案一不一样
            if (cell1.getPattern() != cell2.getPattern()){
                System.out.println("These two are not the same.");
                return false;
            }

            // 计算最小的转弯数
            int bends = minBendsLessThan2(cell1, cell2);

            return bends < 3;
        }

        /**
         * 最短路径问题
         * 首先创建队列，将起始点加进去，然后寻找四周相邻的空格子加进去，将队首出队列。
         * 在队列非空时一直寻找队首格子相邻未找过的空格子加入队列知道寻找到目标点。
         *
         * 数组deep[][]与棋盘格子一一对应，每个点的数字表示与起始点的最短距离，-1表示未遍历过
         *
         * 在寻找最短路径过程中，将最短路径上的点加入path数组中，然后遍历path,记录当前格子的前前格子pre，
         * 如果当前格子的x y 与pre 的x y 都不一样说明转了个弯
         */
        private int minBendsLessThan2(Cell cell1, Cell cell2){
            // 开始判断最短路径的拐弯数
            Queue<Cell> queue = new LinkedList<>(); // 相邻格子队列
            queue.offer(new Cell(cell1.x, cell1.y));

            int[][] deep = new int[scale+2][scale+2]; // 记录路径长度棋盘
            for (int i=0;i<scale+2;i++){
                for (int j=0;j<scale+2;j++){
                    deep[i][j] = -1;
                }
            }
            deep[cell1.x][cell1.y] = 0;

            // 左右上下变化参数
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            path = new ArrayList<>(); // 最短路径格子list
            path.add(cell1);

            while (queue.size() > 0){
                Cell cell = queue.poll();

                if (cell.x == cell2.x && cell.y == cell2.y){
                    break;
                }

                for (int i=0;i<4;i++){
                    int x = cell.x + dx[i];
                    int y = cell.y + dy[i]; // 四周格子坐标
                    if (x >= 0 && x<=scale+1 && y>=0 && y<=scale+1 // 不能超出边界
                            && (board[x][y] == ' ' || (x==cell2.x && y==cell2.y)) // 必须是空格子或者终点格子
                            && deep[x][y] == -1){ // 必须没有走过的格子

                        deep[x][y] = deep[cell.x][cell.y] + 1;

                        if (deep[x][y] > path.size()-1){ // 路径上新格子
                            path.add(cell);
                        }else{
                            path.set(deep[x][y], cell); // 保证路径最短
                        }

                        queue.offer(new Cell(x,y));
                    }
                }
            }

            int bends = 0;
            Cell pre = path.get(0);
            for (int i=2;i<path.size();i++){
                if (path.get(i).x != pre.x && path.get(i).y != pre.y){
                    bends ++;
                }
                pre = path.get(i-1);
            }

            return bends;
        }

        /**
         * 能否继续消除
         */
        private boolean canPlay(){
            for (List<Cell> cells : pairs.values()){
                for (int i=1;i<cells.size();i++){
                    if (canEliminate(cells.get(i-1), cells.get(i))){
                        return true;
                    }
                }
            }
            return false;
        }

        private void reGenerateBoard(){
            char[][] newBoard = new char[scale+2][scale+2];
            List<List<Integer>> positionRemaining = new LinkedList<>();
            for (int i=1;i<=scale;i++){
                List<Integer> column = new LinkedList<>();
                for (int j=1;j<=scale;j++){
                    column.add(j);
                }
                positionRemaining.add(column);
            }

            for (int i=1;i<scale+1;i++){
                for (int j=1;j<scale+1;j++){
                    if (board[i][j] != ' '){
                        int y;
                        do {
                            y = (int) Math.floor(Math.random() * positionRemaining.size());
                        } while (positionRemaining.get(y).size() == 0);

                        int xIndex = (int) Math.floor(Math.random() * positionRemaining.get(y).size());
                        int x = positionRemaining.get(y).get(xIndex);

                        newBoard[x][y] = board[i][j];
                    }
                }
            }

            board = newBoard;
        }

        /**
         * 辅助类
         */
        private class Cell{
            int x;
            int y;

            Cell(int x, int y){
                this.x = x;
                this.y = y;
            }

            char getPattern(){
                return board[x][y];
            }

            @Override
            public String toString(){
                return x + "," + y;
            }
        }
    }
}
