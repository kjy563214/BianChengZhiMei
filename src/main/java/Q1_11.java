import java.util.Arrays;
import java.util.Scanner;

/**
 * 1.11 NIM(1) 一排石头的游戏
 *
 * N 块石头，每块石头有各自固定的位置，两个玩家一次取石头，每个玩家每次可以取其中一块石头，或者相邻的两块石头，
 * 石头在游戏的过程中不能移位（即编号不变），最后能一次取走剩下的石头的玩家获胜。
 * 这个游戏有必胜策略么？
 */
public class Q1_11 {
    public static void main(String[] args){
        Solution1 solution1 = new Solution1();
    }

    /**
     * 先手必胜
     * 从最简单的情况开始考虑
     * 如果只有一个或两个石头，那么先手直接拿走就赢了
     * 如果有三个石头，那么先手拿走中间一个石头，不论后手拿走哪个取走最后一个就赢了。
     * 如果有四个石头，先手拿走中间两个石头，不论后手拿走哪个取走最后一个就赢了。
     * 如果有五个及以上个石头，先手拿走中间的石头(奇数则中间一个，偶数则中间两个)然后与对手拿走以中间为对称的
     * 石头，最后剩两个石头的时候，不论后手拿走哪个取走最后一个就赢了。
     */
    private static class Solution1{

        private Scanner scanner;
        private int numOfStone = -1;
        private int stoneRemain;
        private int[] lastUserAction = new int[0];

        private char[] board;

        Solution1(){

            scanner = new Scanner(System.in);
            System.out.println("Please input number of stones: ");
            String input = scanner.nextLine();

            try{
                numOfStone = Integer.parseInt(input);
            }catch (Exception e){
                return;
            }

            if (numOfStone <= 0){
                return;
            }

            stoneRemain = numOfStone;

            board = new char[numOfStone];
            Arrays.fill(board, 'o');

            while (!isGameFinished()){
                performAction();

                printBoard();
                lastUserAction = null;
                if (isGameFinished()){
                    System.out.println("You lose :(");
                    break;
                }

                performUserAction();
            }

            if (lastUserAction != null){
                System.out.println("You win!");
            }
        }

        private void performAction(){
            if (stoneRemain == numOfStone){
                switch (numOfStone){
                    case 1:
                        System.out.println("System take " + 0);
                        take(0);
                        break;
                    case 2:
                        System.out.println("System take " + 0 + " and " + 1);
                        take(0,1);
                        break;
                    case 3:
                        System.out.println("System take " + 1);
                        take(1);
                        break;
                    case 4:
                        System.out.println("System take " + 1 + " and " + 2);
                        take(1,2);
                        break;
                    default:
                        if (numOfStone % 2 == 0){
                            System.out.println("System take " + numOfStone/2 + " and " + (numOfStone/2 - 1));
                            take(numOfStone/2, numOfStone/2-1);
                        }else{
                            System.out.println("System take " + numOfStone/2);
                            take(numOfStone/2);
                        }
                }
            }else{
                if (lastUserAction.length == 1){
                    System.out.println("System take " + (numOfStone-1 - lastUserAction[0]));
                    take(numOfStone-1 - lastUserAction[0]);
                }else{
                    System.out.println("System take " + (numOfStone-1 - lastUserAction[0])
                            + " and " + (numOfStone-1 - lastUserAction[1]));
                    take(numOfStone-1 - lastUserAction[0], numOfStone-1 - lastUserAction[1]);
                }
            }
        }

        private void performUserAction(){
            while (true){
                System.out.println("Please take 1 or 2 adjacent stones, divide by space, like:1 2");
                String input = scanner.nextLine();
                String[] takes = input.split(" ");
                try{
                    if (takes.length == 1){
                        if (take(Integer.parseInt(takes[0]))){
                            lastUserAction = new int[1];
                            lastUserAction[0] = Integer.parseInt(takes[0]);
                            break;
                        }
                        throw new IllegalArgumentException();
                    }else{
                        if (take(Integer.parseInt(takes[0]),Integer.parseInt(takes[1]))){
                            lastUserAction = new int[2];
                            lastUserAction[0] = Integer.parseInt(takes[0]);
                            lastUserAction[1] = Integer.parseInt(takes[1]);
                            break;
                        }
                        throw new IllegalArgumentException();
                    }
                }catch (Exception e){
                    System.out.println("Invalid input");
                }
            }
        }

        private boolean isGameFinished(){
            return stoneRemain == 0;
        }

        private boolean take(int... index){
            if (index.length > 2){
                return false;
            }
            for (int value : index) {
                if (board[value] == 'o') {
                    board[value] = '-';
                } else {
                    return false;
                }
                System.out.println("take " + value);
                stoneRemain--;
            }
            return true;
        }

        private void printBoard(){
            for (int i=0;i<numOfStone;i++){
                System.out.print(" " + board[i] + " ");
            }
            System.out.println();
            for (int i=0;i<numOfStone;i++){
                System.out.print(" " + i + " ");
            }
            System.out.println();
        }
    }

    /**
     * 如果取走最后一个石头的输呢？
     *
     * 如果只有一个石头，先手必输
     * 如果有两个石头先手必胜
     * 如果有三个石头先手必胜
     * 如果有四个石头先手必输
     * 如果有五个及以上石头，先手可以通过取石头变成上述情况。
     * 比如五个石头，可以拿走一个石头变为四个连续石头也就是让对手处于必输态。
     * 因此如果有N个石头，先手取走1或两个石头后剩下的石头被分成一部分或两部分。
     * 如果是一部分且为必输态，先手必胜否则必输
     * 如果有两部分，
     * 如果一部分是必输态，一部分是必胜态，如果此时对手先取必输态中取，最后的石头肯定会被对手取走，先手必胜，
     *                                  如果此时对手先取必胜态，双方都想在必胜态中败，无法考虑
     * 如果两部分都是必胜，先手必败
     * 两部分都是必输，无法判断
     *
     */
    private static class Question2{

    }
}
