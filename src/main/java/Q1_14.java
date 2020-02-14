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

        private static final char[] CHAR_LIST = {
                '@', '#', '$', '%', '&',
                '(', 'A', 'B', 'C', 'D',
                'E', 'F', 'G', 'H', 'I',
                'K', 'L', 'M', 'N', 'O',
                'P', 'R', 'S', 'T', 'U',
                'W', 'X', 'Y', 'Z', '?'
        };
        private static final int MAX_SCALE = 8;
        private static final int MIN_SCALE = 3;

        char[][] board;
        final int scale;
        final int numOfCell;

        public Solution1(){
            this.scale = getScaleOfBoard();

            numOfCell = scale*scale;
            board = new char[scale+2][scale+2];

            generateGameBoard();

            while (!isGameFinished()){
                printBoard();
                Cell[] cells = getUserSelection();
                if (canEliminate(cells[0], cells[1])){
                    board[cells[0].x][cells[0].y] = ' ';
                    board[cells[1].x][cells[1].y] = ' ';
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

        private void fillBoard(char c, List<List<Integer>> positionRemaining) {
            for (int i = 0; i < 2; i++) { // 每次随机两个位置填充
                int y;
                do {
                    y = (int) Math.floor(Math.random() * positionRemaining.size());
                } while (positionRemaining.get(y).size() == 0);

                int xIndex = (int) Math.floor(Math.random() * positionRemaining.get(y).size());
                int x = positionRemaining.get(y).get(xIndex);

                board[x][y+1] = c;

                positionRemaining.get(y).remove(xIndex);
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
            for (char[] lint : board){
                System.out.println(lint);
            }
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

            int i = minBendsLessThan2(cell1, cell2);

            return i != -1;
        }

        private int minBendsLessThan2(Cell cell1, Cell cell2){
            // 开始判断最短路径的拐弯数
            Queue<Cell> queue = new LinkedList<>();
            queue.offer(new Cell(cell1.x, cell1.y));

            int[][] deep = new int[scale+2][scale+2];
            for (int i=0;i<scale+2;i++){
                for (int j=0;j<scale+2;j++){
                    deep[i][j] = -1;
                }
            }
            deep[cell1.x][cell1.y] = 0;

            // 左右上下变化参数
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            List<Cell> path = new ArrayList<>();
            path.add(cell1);

            while (queue.size() > 0){
                Cell cell = queue.poll();

                if (cell.x == cell2.x && cell.y == cell2.y){
                    break;
                }

                for (int i=0;i<4;i++){
                    int x = cell.x + dx[i];
                    int y = cell.y + dy[i];
                    if (x >= 0 && x<=scale+1 && y>=0 && y<=scale+1
                            && (board[x][y] == ' ' || (x==cell2.x && y==cell2.y))
                            && deep[x][y] == -1){

                        deep[x][y]= deep[cell.x][cell.y] + 1;

                        if (deep[x][y] > path.size()-1){
                            path.add(cell);
                        }else{
                            path.set(deep[x][y], cell);
                        }

                        queue.offer(new Cell(x,y));
                    }
                }
            }

            int bends = 0;
            Cell pre = path.get(0);
            for (int i=2;i<path.size();i++){
                System.out.println(path.get(i).x + " " + path.get(i).y  + "." + pre.x + " " +pre.y);
                if (path.get(i).x != pre.x && path.get(i).y != pre.y){
                    bends ++;
                }
                pre = path.get(i-1);
            }

            System.out.println(bends);

            return deep[cell2.x][cell2.y];
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
