package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 给定一个数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。
 * <p>
 * candidates 中的每个数字在每个组合中只能使用一次。
 * <p>
 * 说明：
 * <p>
 * 所有数字（包括目标数）都是正整数。
 * 解集不能包含重复的组合。 
 * 示例 1:
 * <p>
 * 输入: candidates = [10,1,2,7,6,1,5], target = 8,
 * 所求解集为:
 * [
 * [1, 7],
 * [1, 2, 5],
 * [2, 6],
 * [1, 1, 6]
 * ]
 * 示例 2:
 * <p>
 * 输入: candidates = [2,5,2,1,2], target = 5,
 * 所求解集为:
 * [
 *   [1,2,2],
 *   [5]
 * ]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/combination-sum-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class CombinationSum2 {
    private List<List<Integer>> result = new ArrayList<>();

    // 整体思路，先排序，然后取子集，并计算track和，循环时跳过重复项
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        LinkedList<Integer> track = new LinkedList<>();
        Arrays.sort(candidates);
        backward(candidates, 0, target, track);

        return result;
    }

    private void backward(int[] nums, int start, int target, LinkedList<Integer> track) {
        if (track.size() > 0) {
            int sum = track.stream().mapToInt(i -> i).sum();
            if (sum == target) {
                result.add(new ArrayList<>(track));
                return;
            }
            // 如果当前递归的总和已经超过target，那么就不必继续递归了，肯定不符合
            if (sum > target) {
                return;
            }
        }
        int pre = -1;
        for (int i = start; i < nums.length; i++) {
            // 重复的项前面已经计算过，跳过
            if (pre == nums[i]){
                continue;
            }
            pre = nums[i];
            track.add(nums[i]);
            backward(nums, i + 1, target, track);
            track.removeLast();
        }
    }
}
