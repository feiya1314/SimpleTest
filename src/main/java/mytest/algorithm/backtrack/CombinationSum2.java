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

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> track = new ArrayList<>();
        // 由于可能有重复的，所以先排序，重复值直接跳过
        Arrays.sort(candidates);
        backtrack(candidates, 0, 0, target, result, track);
        return result;
    }

    private void backtrack(int[] nums, int start, int sum, int target, List<List<Integer>> result, List<Integer> track) {
        // 如果target中的元素之和达成，则加入结果
        if (!track.isEmpty()) {
            if (sum == target) {
                result.add(new ArrayList<>(track));
            }
            if (sum > target) {
                return;
            }
        }
        // 寻找所有可行解的题，我们都可以尝试用「搜索回溯」的方法来解决
        // 1）当组合中允许元素重复：则迭代的起始值就是i，每次递增
        // 2）当组合中不允许重复：迭代的起始值是i + 1，每次递增
        // 3）当这是排列不是组合：迭代的起始值是传进去的start（初始位），每次都从头开始遍历所有元素

        // 这里i从start开始，是因为前面已经重复计算过了，例如 2 2 3 和 2 3 2 ，重复了，需要剪枝，
        // 每次开始时从start开始，避免重复，例如 2 2 3，下一次直接从 2 3 3 开始
        // 深度优先遍历
        int pre = -1;
        for (int i = start; i < nums.length; i++) {
            // 和上一个重复的话，不再计算了，会重复
            if (pre == nums[i]) {
                continue;
            }
            pre = nums[i];
            track.add(nums[i]);
            // 这里因为之前用过的不能重复使用，所以需要i + 1
            backtrack(nums, i + 1, sum + nums[i], target, result, track);
            track.removeLast();
            // 由于已经排序了，后续的再加肯定不满足了
            if (sum + nums[i] >= target) {
                break;
            }
        }
    }

    // 整体思路，先排序，然后取子集，并计算track和，循环时跳过重复项
    public List<List<Integer>> combinationSum22(int[] candidates, int target) {
        LinkedList<Integer> track = new LinkedList<>();
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backward2(candidates, 0, target, result, track);

        return result;
    }

    private void backward2(int[] nums, int start, int target, List<List<Integer>> result, LinkedList<Integer> track) {
        if (!track.isEmpty()) {
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
            if (pre == nums[i]) {
                continue;
            }
            pre = nums[i];
            track.add(nums[i]);
            backward2(nums, i + 1, target, result, track);
            track.removeLast();
        }
    }
}
