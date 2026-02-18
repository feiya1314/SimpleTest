package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 给定一个无重复元素的数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。
 * <p>
 * candidates 中的数字可以无限制重复被选取。
 * <p>
 * 说明：
 * <p>
 * 所有数字（包括 target）都是正整数。
 * 解集不能包含重复的组合。 
 * 示例 1：
 * <p>
 * 输入：candidates = [2,3,6,7], target = 7,
 * 所求解集为：
 * [
 * [7],
 * [2,2,3]
 * ]
 * 示例 2：
 * <p>
 * 输入：candidates = [2,3,5], target = 8,
 * 所求解集为：
 * [
 *   [2,2,2,2],
 *   [2,3,3],
 *   [3,5]
 * ]
 *  
 * <p>
 * 提示：
 * <p>
 * 1 <= candidates.length <= 30
 * 1 <= candidates[i] <= 200
 * candidate 中的每个元素都是独一无二的。
 * 1 <= target <= 500
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/combination-sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : yufei
 * @date : 2020/12/24 10:14
 * @description :
 */
public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> track = new LinkedList<>();
        Arrays.sort(candidates);
        backtrack(candidates, 0, target, 0, result, track);
        return result;
    }

    private void backtrack(int[] nums, int start, int target, int sum, List<List<Integer>> result, LinkedList<Integer> track) {
        // track 中总数大于等于target 时，没必要继续往下递归了
        if (!track.isEmpty()) {
            if (sum == target) {
                result.add(new ArrayList<>(track));
                return;
            }
            if (sum > target) {
                return;
            }
        }

        // 寻找所有可行解的题，我们都可以尝试用「搜索回溯」的方法来解决
        // 1）当组合中允许元素重复：则迭代的起始值就是i，每次递增
        // 2）当组合中不允许重复：迭代的起始值是i + 1，每次递增
        // 3）当这是排列不是组合：迭代的起始值是传进去的start（初始位），每次都从头开始遍历所有元素

        // 每次都是从i处开始组合 例如 2 3 5
        // 2 的分支 start 都是从 【0】 开始，每次递归都从2 开始 比如 2222 2223
        // 而 3的分支都从【1】开始 ，每次递归都忽略2的 例如 2233 2235 2333 23335  3333 3335
        // 5也一样
        // 这里i从start开始，是因为前面已经重复计算过了，例如 2 2 3 和 2 3 2 ，重复了，需要剪枝，
        // 每次开始时从start开始，避免重复，例如 2 2 3，下一次直接从 2 3 3 开始
        // 深度优先遍历
        for (int i = start; i < nums.length; i++) {
            // 值为0的分支没必要 例如 0 0 2 ，0 0 3 ，0 0 5 ， 0 2 3 ，0 2 5，最终包含0 的分支都是非必要的，
            // 并且 0 会无限递归
            if (nums[i] == 0) {
                continue;
            }
            track.add(nums[i]);
            // 上一层sum加上当前的sum,保留之前的计算结果
            backtrack(nums, i, target, sum + nums[i], result, track);
            track.removeLast();
            // 排序后，当前的值大于等于target了，后续的值只能会更大，所以没必要再继续了，直接跳过了
            if (sum + nums[i] >= target) {
                break;
            }
        }
    }


    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(candidates, 0, target, result, track);
        return result;
    }

    private void backtrack(int[] nums, int start, int target, List<List<Integer>> result, LinkedList<Integer> track) {
        // track 中总数大于等于target 时，没必要继续往下递归了
        if (!track.isEmpty()) {
            int sum = track.stream().mapToInt(i -> i).sum();
            if (sum == target) {
                result.add(new ArrayList<>(track));
                return;
            }
            if (sum > target) {
                return;
            }
        }

        // 每次都是从i处开始组合 例如 2 3 5
        // 2 的分支 start 都是从 【0】 开始，每次递归都从2 开始 比如 2222 2223
        // 而 3的分支都从【1】开始 ，每次递归都忽略2的 例如 2233 2235 2333 23335  3333 3335
        // 5也一样
        // 这里i从start开始，是因为前面已经重复计算过了，例如 2 2 3 和 2 3 2 ，重复了，需要剪枝，
        // 每次开始时从start开始，避免重复，例如 2 2 3，下一次直接从 2 3 3 开始
        // 深度优先遍历
        for (int i = start; i < nums.length; i++) {
            // 值为0的分支没必要 例如 0 0 2 ，0 0 3 ，0 0 5 ， 0 2 3 ，0 2 5，最终包含0 的分支都是非必要的，
            // 并且 0 会无限递归
            if (nums[i] == 0) {
                continue;
            }
            track.add(nums[i]);
            backtrack(nums, i, target, result, track);
            track.removeLast();
        }
    }
}
