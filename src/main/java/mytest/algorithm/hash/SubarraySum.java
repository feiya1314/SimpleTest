package mytest.algorithm.hash;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.cn/problems/subarray-sum-equals-k/?envType=study-plan-v2&envId=top-100-liked
 */
public class SubarraySum {
    public int subarraySum(int[] nums, int k) {
        //对于数组中的任何位置 j，前缀和 pre[j] 是数组中从第一个元素到第 j 个元素的总和。
        // 这意味着如果你想知道从元素 i+1 到 j 的子数组的和，你可以用 pre[j] - pre[i] 来计算。
        // 【preSum[j]】  ｜———————前面一段总和 sum-k ———————｜——— k ———|
        // 【preSum[i]】  ｜——————————————整段总和 sum ————————————————|
        //                          ↓
        // ｜——— k ———| 这一段数组是连续的，并且k中元素个数不确定，
        // 当前缀和 sum时，如果前缀和sum-k存在，说明当前位置一定存在连续的元素和等于k，
        // 例如 1 -1 2 3 -2 4 ，k=3 当遍历到 -2 时，当前前缀和是3，需要找，sum - k = 3 -3 = 的前缀和，
        // 发现前缀和等于0 有两个，说明存在两个连续的区间 ｜——— k ———|，使得条件满足，这两个区间k中元素个数不一样
        // 2 3 -2 和 1 -1 2 3 -2
        int count = 0;
        int curSum = 0;
        Map<Integer, Integer> sumMap = new HashMap<>();
        // 第一个元素前面的前缀和是 0 ，例如1 -1 2，k=1，第一个1就是满足条件的
        sumMap.put(0, 1);
        for (int i = 0; i < nums.length; i++) {
            curSum += nums[i];
            if (sumMap.containsKey(curSum - k)) {
                count = count + sumMap.get(curSum - k);
            }
            sumMap.put(curSum, sumMap.getOrDefault(curSum, 0) + 1);
        }
        return count;
    }

    public int subarraySum1(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            // 从当前值开始，一直往后累加，数组是连续的，统计值 == k的
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k) {
                    count++;
                }
            }
        }

        return count;
    }
}
