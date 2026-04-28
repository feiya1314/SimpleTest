package mytest.algorithm.hash;

import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode.cn/problems/longest-consecutive-sequence/description/?envType=study-plan-v2&envId=top-100-liked
 * 最长连续序列
 */
public class LongestConsecutive {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        int max = 0;
        // 注意！！！ 这里使用set，去重后的数据，重复的数据不用再次统计
        for (int num : set) {
            // 如果num-1在set中，则当前num统计的最优结果也不如num-1
            if (set.contains(num - 1)) {
                continue;
            }
            int index = num;
            int count = 1;
            while (set.contains(index + 1)) {
                count++;
                index++;
            }
            max = Math.max(max, count);
        }
        return max;
    }

}
