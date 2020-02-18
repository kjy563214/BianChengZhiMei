import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1.15 构造数独
 * 如何设计一个数独游戏？
 * 包括
 * 1. 程序的大致框架是什么？
 * 2. 用什么样的数据结构存储游戏中的各种元素？
 * 3. 如何生成初始局面？
 */

/**
 * 1. 如何通过回溯法构造(并不是树形回溯法)
 * 2. 通过取一部分特殊解来避免复杂算法(Solution2)
 */
public class Q1_15 {
    public static void main(String[] args) {
        Solution1 solution1 = new Solution1();
        //Solution2 solution2 = new Solution2();
    }

    /**
     * 回溯法
     * 遍历到一个格子的时候记录回溯次数，重复走过就+1，回溯的时候回溯到这个数字的格子之前，只是为了避免卡循环，
     * 如果只回溯一个格子就会在两个值间卡住。不是最佳方案, State是为了做游戏，没有用
     */
    private static class Solution1 {

        private static final int HOLES = 30;
        private static final int BOARD_SIZE = 9;
        private Cell[][] board;
        private Cell[][] oneAnswer;

        public Solution1() {
            generateBoard();
            System.out.println(isGameFinished());

            oneAnswer = new Cell[BOARD_SIZE][BOARD_SIZE];
            for (int i=0;i<BOARD_SIZE;i++){
                System.arraycopy(board[i], 0, oneAnswer[i], 0, BOARD_SIZE);
            }

            makeHoles();

            printBoard();
        }

        private void makeHoles() {
            int counter = HOLES;

            List<List<Integer>> numsAva = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                List<Integer> line = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    line.add(j);
                }
                numsAva.add(line);
            }

            while (counter > 0) {
                int row = -1;

                while (row < 0) {
                    int rowIndex = (int) Math.floor(Math.random() * numsAva.size());
                    if (numsAva.get(rowIndex).size() == 0) {
                        row = -1;
                        continue;
                    }

                    row = rowIndex;
                }

                int columnIndex = (int) Math.floor(Math.random() * numsAva.get(row).size());
                int column = numsAva.get(row).get(columnIndex);

                board[row][column].num = 0;

                numsAva.get(row).remove(columnIndex);
                counter --;
            }
        }

        private void generateBoard() {
            board = new Cell[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    board[i][j] = new Cell(j, i, 0);
                }
            }

            Cell c = board[0][0];
            while (true) {

                List<Integer> numberAvailable = findNumberAvailable(c);

                if (numberAvailable.size() == 0) {
                    c.traceNum++;
                    c = traceBack(c, c.traceNum);
                } else {
                    fillInNum(c, numberAvailable);
                }

                if (c.column == BOARD_SIZE -1 & c.row == BOARD_SIZE - 1){
                    break;
                }

                c = findNextCell(c);
            }
        }

        private List<Integer> findNumberAvailable(Cell c) {
            List<Integer> available = new ArrayList<>();

            for (int i = 1; i <= 9; i++) {
                available.add(i);
            }

            // 检查列
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[c.row][i].num != 0) {
                    for (int j = 0; j < available.size(); j++) {
                        if (available.get(j) == board[c.row][i].num) {
                            available.remove(j);
                            break;
                        }
                    }
                }
            }

            // 检查行
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i][c.column].num != 0) {
                    for (int j = 0; j < available.size(); j++) {
                        if (available.get(j) == board[i][c.column].num) {
                            available.remove(j);
                            break;
                        }

                    }
                }
            }

            // 检查九宫格内
            for (int i = c.row / 3 * 3; i < (c.row / 3 + 1) * 3; i++) {
                for (int j = c.column / 3 * 3; j < (c.column / 3 + 1) * 3; j++) {
                    if (board[i][j].num != 0) {
                        for (int k = 0; k < available.size(); k++) {
                            if (available.get(k) == board[i][j].num) {
                                available.remove(k);
                                break;
                            }
                        }
                    }
                }
            }


            return available;
        }

        /**
         * 回溯 num 个格子, 返回回溯的格子
         */
        private Cell traceBack(Cell currentCell, int num) {
            Cell cursor = currentCell;
            currentCell.num = 0;
            for (int i = 0; i < num; i++) {
                cursor = findPreviousCell(cursor);
                fillInNum(cursor, new ArrayList<Integer>(){{
                    this.add(0);
                }});
            }

            fillInNum(cursor, findNumberAvailable(cursor));

            return cursor;
        }

        /**
         * 找到当前格子下一个格子
         */
        private Cell findPreviousCell(Cell cur) {
            int x = cur.column;
            int y = cur.row;

            if (x == 0 && y == 0){
                return board[0][0];
            }

            if (y == 0) {
                return board[BOARD_SIZE - 1][--x];
            } else {
                return board[--y][x];
            }
        }

        /**
         * 找到当前格子前一个格子
         */
        private Cell findNextCell(Cell cur) {
            int x = cur.column;
            int y = cur.row;

            if (y == BOARD_SIZE - 1) {
                return board[0][++x];
            } else {
                return board[++y][x];
            }
        }

        /**
         * 从可选数字中选一个填进给定格子里
         */
        private void fillInNum(Cell c, List<Integer> numberAvailable) {
            c.num = numberAvailable.get((int) Math.floor(Math.random() * numberAvailable.size()));
        }

        private void printBoard() {
            System.out.println();
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j].num == 0) {
                        System.out.print("  ");
                    }else{
                        System.out.print(board[i][j].num + " ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }

        private boolean isGameFinished() {
            return isRowsComplete() && isColumnsComplete() && isBoxesComplete();
        }

        private boolean isRowsComplete() {
            for (int i = 0; i < 9; i++) {
                List<Integer> nums = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    if (nums.contains(board[i][j].num)) {
                        return false;
                    }
                    nums.add(board[i][j].num);
                }
            }
            return true;
        }

        private boolean isColumnsComplete() {
            for (int i = 0; i < 9; i++) {
                List<Integer> nums = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    if (nums.contains(board[j][i].num)) return false;
                    nums.add(board[j][i].num);
                }
            }
            return true;
        }

        private boolean isBoxesComplete() {
            for (int index = 0; index < 9; index++) {
                Cell[][] box = getBoxAt(index);

                List<Integer> nums = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (nums.contains(box[i][j].num)) return false;
                        nums.add(box[i][j].num);
                    }
                }
            }
            return true;
        }

        private Cell[][] getBoxAt(int index) {
            Cell[][] box = new Cell[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(board[index / 3 * 3 + i], index % 3 * 3, box[i], 0, 3);
            }

            return box;
        }

        private class Cell {

            State state;
            int num;
            int column;
            int row;
            int traceNum;

            public Cell(int column, int row, int num) {
                this.column = column;
                this.row = row;
                this.num = num;
                this.traceNum = 0;
            }

            @Override
            public String toString() {
                return row + "." + column + " : " + num;
            }
        }

        private enum State {
            DEFAULT, USER_INPUT
        }
    }

    /**
     * 随机生成一个 3*3 的包含 1-9 且不重复的矩阵，然后通过变换行列位置生成整个 9*9 数独矩阵，并不能生成全部解
     */
    private static class Solution2 {

        private final int NUM_HOLES = 30;

        int[][] board;

        public Solution2() {
            generateBoard();
            makeHoles();
            printBoard();

            System.out.println(isGameFinished());
        }

        private void generateBoard() {
            board = new int[9][9];

            // 首先生成 3*3 随机小矩阵
            int[][] prototype = generatePrototype();
            fillToBoard(4, prototype);

            // 变换生成左右矩阵 (B3,B5)
            generate35(prototype);

            // 变换生成上下矩阵
            generate17(prototype);

            // 变换生成4个角
            generate1268(0, getBoxAt(1));
            generate1268(2, getBoxAt(1));
            generate1268(6, getBoxAt(7));
            generate1268(8, getBoxAt(7));
        }

        private int[][] generatePrototype() {
            int[][] board33 = new int[3][3];
            List<Integer> nums = new ArrayList<Integer>();
            for (int i = 1; i <= 9; i++) {
                nums.add(i);
            }

            int row = 0;
            int column = 0;
            while (nums.size() > 0) {
                int index = (int) Math.floor(Math.random() * nums.size());
                board33[row][column] = nums.get(index);
                column += (row + 1) / 3;
                row = (row + 1) % 3;
                nums.remove(index);
            }

            return board33;
        }

        private void generate35(int[][] prototype) {
            int[][] change33 = new int[3][3];
            for (int index = 1; index < 3; index++) {
                for (int i = 0; i < 3; i++) {
                    change33[i] = prototype[(i + (3 - index)) % 3];
                }
                fillToBoard(3 + (index - 1) * 2, change33);
            }
        }

        private void generate17(int[][] prototype) {
            int[][] change33 = new int[3][3];

            for (int index = 1; index < 3; index++) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        change33[i][j] = prototype[i][(j + (3 - index)) % 3];
                    }
                }
                fillToBoard(1 + (index - 1) * 6, change33);
            }
        }

        private void generate1268(int index, int[][] prototype) {
            int[][] change33 = new int[3][3];

            int change = 2;
            if (index == 2 || index == 8) change = 1;

            for (int i = 0; i < 3; i++) {
                change33[i] = prototype[(i + change) % 3];
            }

            fillToBoard(index, change33);
        }

        private void fillToBoard(int index, int[][] box) {
            for (int i = 0; i < 3; i++) {
                System.arraycopy(box[i], 0, board[index / 3 * 3 + i], index % 3 * 3, 3);
            }
        }

        private int[][] getBoxAt(int index) {
            int[][] box = new int[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(board[index / 3 * 3 + i], index % 3 * 3, box[i], 0, 3);
            }

            return box;
        }

        private boolean isGameFinished() {
            return isRowsComplete() && isColumnsComplete() && isBoxesComplete();
        }

        private boolean isRowsComplete() {
            for (int i = 0; i < 9; i++) {
                List<Integer> nums = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    if (nums.contains(board[i][j])) {
                        return false;
                    }
                    nums.add(board[i][j]);
                }
            }
            return true;
        }

        private boolean isColumnsComplete() {
            for (int i = 0; i < 9; i++) {
                List<Integer> nums = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    if (nums.contains(board[j][i])) return false;
                    nums.add(board[j][i]);
                }
            }
            return true;
        }

        private boolean isBoxesComplete() {
            for (int index = 0; index < 9; index++) {
                int[][] box = getBoxAt(index);

                List<Integer> nums = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (nums.contains(box[i][j])) return false;
                        nums.add(box[i][j]);
                    }
                }
            }
            return true;
        }

        private void makeHoles() {
            int counter = NUM_HOLES;

            List<List<Integer>> numsAva = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                List<Integer> line = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    line.add(j);
                }
                numsAva.add(line);
            }

            while (counter > 0) {
                int row = -1;

                while (row < 0) {
                    int rowIndex = (int) Math.floor(Math.random() * numsAva.size());
                    if (numsAva.get(rowIndex).size() == 0) {
                        row = -1;
                        continue;
                    }

                    row = rowIndex;
                }

                int columnIndex = (int) Math.floor(Math.random() * numsAva.get(row).size());
                int column = numsAva.get(row).get(columnIndex);

                board[row][column] = 0;

                numsAva.get(row).remove(columnIndex);
                counter --;
            }
        }

        private void printBoard() {
            for (int[] row : board) {
                for (int cell : row) {
                    if (cell == 0) {
                        System.out.print("  ");
                    } else {
                        System.out.print(cell + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}
