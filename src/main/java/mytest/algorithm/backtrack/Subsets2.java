package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * //给定一个可能包含重复元素的整数数组 nums，返回该数组所有可能的子集（幂集）。
 * //
 * // 说明：解集不能包含重复的子集。
 * //
 * // 示例:
 * //
 * // 输入: [1,2,2]
 * //输出:
 * //[
 * //  [2],
 * //  [1],
 * //  [1,2,2],
 * //  [2,2],
 * //  [1,2],
 * //  []
 * //]
 * // Related Topics 数组 回溯算法
 * https://leetcode-cn.com/problems/subsets-ii/
 *
 * @author : yufei
 * @date : 2020/12/26 16:36
 * @description :
 */
public class Subsets2 {
    List<List<Integer>> result = new ArrayList<>();

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        LinkedList<Integer> track = new LinkedList<>();
        Arrays.sort(nums);
        backtrack(nums, 0, track);
        return result;
    }

    private void backtrack(int[] nums, int start, LinkedList<Integer> track) {
        result.add(new ArrayList<>(track));

        // pre记录已经处理过的数，重复了，后续不用处理，跳过
        Integer pre = null;
        for (int i = start; i < nums.length; i++) {
            if (pre != null && nums[i] == pre) {
                continue;
            }
            track.add(nums[i]);
            pre = nums[i];
            // 已经使用过的数字，在往下递归的时候就不再使用了
            backtrack(nums, i + 1, track);
            track.removeLast();
        }
    }
}
