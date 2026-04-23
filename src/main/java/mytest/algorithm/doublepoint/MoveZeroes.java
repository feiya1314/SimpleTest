package mytest.algorithm.doublepoint;

/**
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,3,12]
 * 输出: [1,3,12,0,0]
 * 说明:
 * <p>
 * 必须在原数组上操作，不能拷贝额外的数组。
 * 尽量减少操作次数。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/move-zeroes
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/10
 * @description :
 */
public class MoveZeroes {
    // 双指针，整体思想是把非 0 数移动到左边，左指针记录下一次可以移动的数字的位置，左指针前的数都是非0数，
    // 右指针遍历，最后把左指针及之后的位置补 0
    public void moveZeroes2(int[] nums) {
        if (nums == null || nums.length < 2) {
            return;
        }
        int left = 0;
        int right = 0;
        for (right = 0; right < nums.length; right++) {
            // 如果当前遍历的数不等于 0，则把这个数字移动到left处，同时，left右移，以便下一次非0数移动到left处
            if (nums[right] != 0) {
                nums[left] = nums[right];
                left++;
            }
            // 如果当前数等于0，左指针不动，等待下一个非 0 的数，移动到left处
        }
        while (left < nums.length) {
            nums[left] = 0;
            left++;
        }
    }

    public void moveZeroes(int[] nums) {
        int slowP = 0;
        int fastP = 0;
        // 一次遍历，如果当前值是0，则快指针往前走，如果不是0，则两个指针交换值，继续往下走
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                fastP++;
                continue;
            }
            // 如果同一个位置，不用交换，继续往下
            if (fastP == slowP) {
                slowP++;
                fastP++;
                continue;
            }
            // 不是同一个位置，说明慢指针指向0的位置，交换下值，当前i位置置为 0，注意这种方式fastP == slowP必须要有
            // 不然相同位置，但是不为0的情况，会替换成0
            nums[slowP] = nums[i];
            nums[i] = 0;
            slowP++;
            fastP++;
        }
    }

    public void moveZeroes3(int[] nums) {
        int slowP = 0;
        int fastP = 0;
        // 一次遍历，如果当前值是0，则快指针往前走，如果不是0，则两个指针交换值，继续往下走
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                fastP++;
                continue;
            }

            // 不为0 直接交换，这里覆盖了两种情况，两个指针位置相同和位置不同，此时交换效果都是一样
            int temp = nums[slowP];
            nums[slowP] = nums[i];
            nums[i] = temp;
            slowP++;
            fastP++;
        }
    }
}
