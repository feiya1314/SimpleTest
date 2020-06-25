package mytest.algorithm.dp;

/**
 * 给定一个非负整数数组，你最初位于数组的第一个位置。
 * <p>
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 * <p>
 * 判断你是否能够到达最后一个位置。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [2,3,1,1,4]
 * 输出: true
 * 解释: 我们可以先跳 1 步，从位置 0 到达 位置 1, 然后再从位置 1 跳 3 步到达最后一个位置。
 * 示例 2:
 * <p>
 * 输入: [3,2,1,0,4]
 * 输出: false
 * 解释: 无论怎样，你总会到达索引为 3 的位置。但该位置的最大跳跃长度是 0 ， 所以你永远不可能到达最后一个位置。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/jump-game
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class CanJump {
    public static void main(String[] args) {
        int[] arr = new int[]{3,2,1,0,4};
        System.out.println(canJump(arr));
    }

    // 记录每一次跳能跳的最远位置，
    public static boolean canJump(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        if (nums.length == 1) {
            return true;
        }

        int mostFarIndex = 0;
        int endIndex = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            if (i == mostFarIndex && nums[i] == 0) { // 当前面元素能跳的最远位置和当前位置重合，并且当前位置为0时，此时不能继续往下跳了，如果为0，到达不了结尾
                return false;
            }

            int curFarIndex = i + nums[i]; // 计算当前位置能最远跳多远，如果能到达末端，就不用继续往下了，直接返回
            if (curFarIndex >= endIndex) {
                return true;
            }

            if (curFarIndex > mostFarIndex) { // 更新最远的位置
                mostFarIndex = curFarIndex;
            }
        }

        return false;
    }
}
