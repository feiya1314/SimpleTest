package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 求不重复数组的所有不空的子集
 * 比如输入 nums = [1,2,3]，你的算法应输出 8 个子集，包含空集和本身，顺序可以不同：
 *
 * [ [],[1],[2],[3],[1,3],[2,3],[1,2],[1,2,3] ]
 *
 * @author : yufei
 * @date : 2020/12/24 10:23
 * @description :
 */
public class SubSets {
    List<List<Integer>> result = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(new SubSets().subSets(new int[]{2, 3, 6}));
    }

    public List<List<Integer>> subSets(int[] nums) {
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(nums, 0, track);
        return result;
    }

    // 不包含空集
    private void backtrack(int[] nums, int start, LinkedList<Integer> track) {
        // 如果需要包含空集，可以把if条件去掉
        if (track.size() > 0) {
            result.add(new ArrayList<>(track));
        }
        // 下面的判断也可以不用，到for循环的时候，start 自然超出num length
        //if (track.size()>nums.length){
        //return;
        //}

        for (int i = start; i < nums.length; i++) {
            track.add(nums[i]);
            // 每次递归从下一个字符开始
            backtrack(nums, i + 1, track);
            // 递归后移除本次追踪的，例如 236，如果上面add 2之后，经过递归，
            // 最后一位依然是2，最后移除 2，下一次track 最后一位是从3开始了
            track.removeLast();
        }
    }
}
