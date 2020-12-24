package mytest.algorithm.backtrack;

import java.util.ArrayList;
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
    private final List<List<Integer>> result = new ArrayList<>();

    public List<List<Integer>> combinationSum(int[] candidates, int target) {

        LinkedList<Integer> track = new LinkedList<>();
        backtrack(candidates, 0, target, track);
        return result;
    }

    private void backtrack(int[] nums, int start, int target, LinkedList<Integer> track) {
        // track 中总数大于等于target 时，没必要继续往下递归了
        if (track.size() > 0) {
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
        for (int i = start; i < nums.length; i++) {
            if (nums[i] == 0){
                continue;
            }
            track.add(nums[i]);
            backtrack(nums, i, target, track);
            track.removeLast();
        }
    }
}
