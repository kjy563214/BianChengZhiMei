import java.util.*;

/**
 * Q2.3 寻找发帖水王
 *
 * 在贴吧中有一个水王，他不但喜欢发帖，还喜欢回复其他ID发的每个帖子。该水王发帖数目 “超过了帖子总数一半”。
 * 如果给定一个当前论坛上所有帖子列表(包括回帖)，其中帖子作者id也在其中，如何快速找出水王？
 */

/**
 * 1. 计数Map酷炫解决方案 nums.merge(id, 1, Integer::sum);
 * 2. 将大问题转换为小问题
 * 3. Solution3如何巧妙利用times计数来达成消除目的
 */
public class Q2_3 {
    public static void main(String[] args){
        List<Integer> idsOfPosts = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (int i=0;i<10000;i++){
            idsOfPosts.add(i);
            ids.add(i);
        }

        for (int i=0;i<5200;i++){
            int index = (int) Math.floor(Math.random()*ids.size());
            idsOfPosts.set(ids.get(index), 250);
            ids.remove(index);
        }

        solution1(idsOfPosts);
        solution2(idsOfPosts);
        solution3(idsOfPosts);
    }

    /**
     * 最简单的方法排序然后统计id出现次数，如果有一个超过一半了就是水王
     */
    private static void solution1(List<Integer> idsOfPosts){
        Map<Integer, Integer> nums = new HashMap<>();

        Collections.sort(idsOfPosts);
        for (int id : idsOfPosts){

            nums.merge(id, 1, Integer::sum);
            if (nums.get(id) > idsOfPosts.size() / 2){
                System.out.println("Id of the waterking is: " + id);
                return;
            }
        }
    }

    /**
     * 对于一个排序数组，那么第 N/2 个数一定那个出现次数大于一半的数
     */
    private static void solution2(List<Integer> idsOfPosts){
        Collections.sort(idsOfPosts);

        System.out.println("Id of the waterking is: " + idsOfPosts.get(idsOfPosts.size() / 2));
    }

    /**
     * 为了避免排序，我们考虑其他解决方案。
     * 如果同时删除两个不同id，那么在剩下的数组中水王id出现次数依然大于一半，如此不断缩减总数得到答案
     */
    private static void solution3(List<Integer> idsOfPosts){
        int id = 0;
        int times = 0;
        for (int i=0;i<idsOfPosts.size();i++){
            if (times == 0){ // 记录新id
                id = idsOfPosts.get(i);
                times = 1;
            }else{
                if (id == idsOfPosts.get(i)){ // 如果当前id又出现了，次数+1
                    times++;
                }else{ // 如果出现了一个不是当前id的，次数-1，相当于消除了一组出现过和没出现过的id
                    times--;
                }
            }
        }

        System.out.println("Id of the waterking is: " + id);
    }
}
