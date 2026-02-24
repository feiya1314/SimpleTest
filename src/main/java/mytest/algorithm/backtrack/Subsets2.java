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
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> track = new ArrayList<>();
        // 由于可能有重复的，所以先排序，后续方便去除重复的
        Arrays.sort(nums);
        backtrack(nums, 0, result, track);

        return result;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> result, List<Integer> track) {
        //
        result.add(new ArrayList<>(track));

        int pre = Integer.MAX_VALUE;
        for (int i = start; i < nums.length; i++) {
            // 这里可以使用nums[i] == nums[i-1] 是因为不会跳过重复的前面的，例如1 2 2 2  同一层中，第一个2 前面肯定会读到，第二个 2 会跳过，第三个2也即会跳过
            // 与mytest.algorithm.backtrack.Permutation2.permute 中排列不同的是，排列中，第一个2 可能跳过，
            // 因为2坐标处已经使用过，但是第二个2没有使用过，但是因为和前一个相等，导致跳过，最终所有的2都被跳过
            if (i> start && nums[i] == nums[i-1]) {
                continue;
            }
//            if (pre == nums[i]) {
//                continue;
//            }
            pre = nums[i];
            track.add(nums[i]);
            backtrack(nums, i + 1, result, track);
            track.removeLast();
        }
    }

    public List<List<Integer>> subsetsWithDup2(int[] nums) {
        LinkedList<Integer> track = new LinkedList<>();
        // 由于可能有重复的，所以先排序，去除重复的
        Arrays.sort(nums);
        backtrack2(nums, 0, track);
        return result;
    }

    private void backtrack2(int[] nums, int start, LinkedList<Integer> track) {
        result.add(new ArrayList<>(track));

        // pre记录已经处理过的数，重复了，后续不用处理，跳过
        for (int i = start; i < nums.length; i++) {
            //
            if (i> start && nums[i] == nums[i-1]) {
                continue;
            }
            track.add(nums[i]);
            // 已经使用过的数字，在往下递归的时候就不再使用了
            backtrack2(nums, i + 1, track);
            track.removeLast();
        }
    }
}
