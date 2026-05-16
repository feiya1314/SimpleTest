package mytest.algorithm.array;

/**
 * https://leetcode.cn/problems/majority-element/?envType=study-plan-v2&envId=top-100-liked
 */
public class MajorityElement {
    public int majorityElement(int[] nums) {
        // cur记录最多数，不同的值兑子，兑完后，剩下的肯定是次数最多的那个
        int cur = nums[0];
        int curCnt = 1;
        for (int i = 1; i < nums.length; i++) {
            if (curCnt == 0) {
                cur = nums[i];
                curCnt = 1;
                continue;
            }
            // 如果和cur相同，则频次数加1，否则减一
            if (nums[i] == cur) {
                curCnt++;
            } else {
                curCnt--;
            }
        }
        return cur;
    }
}
