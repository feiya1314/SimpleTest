package mytest.algorithm.tree.heap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 给你一个整数数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
 * <p>
 * 返回滑动窗口中的最大值。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [1,3,-1,-3,5,3,6,7], k = 3
 * 输出：[3,3,5,5,6,7]
 * 解释：
 * 滑动窗口的位置                最大值
 * ---------------               -----
 * [1  3  -1] -3  5  3  6  7       3
 * 1 [3  -1  -3] 5  3  6  7       3
 * 1  3 [-1  -3  5] 3  6  7       5
 * 1  3  -1 [-3  5  3] 6  7       5
 * 1  3  -1  -3 [5  3  6] 7       6
 * 1  3  -1  -3  5 [3  6  7]      7
 * 示例 2：
 * <p>
 * 输入：nums = [1], k = 1
 * 输出：[1]
 * 示例 3：
 * <p>
 * 输入：nums = [1,-1], k = 1
 * 输出：[1,-1]
 * 示例 4：
 * <p>
 * 输入：nums = [9,11], k = 2
 * 输出：[11]
 * 示例 5：
 * <p>
 * 输入：nums = [4,-2], k = 2
 * 输出：[4]
 *  
 * <p>
 * 提示：
 * <p>
 * 1 <= nums.length <= 105
 * -104 <= nums[i] <= 104
 * 1 <= k <= nums.length
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sliding-window-maximum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/12
 * @description :
 */
public class MaxSlidingWindow {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(new MaxSlidingWindow().maxSlidingWindow(new int[]{3, 1, -1, -3, -4, -5, -6, -7}, 3)));
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(10, (o1, o2) -> o2 - o1);
        for (int i = 0; i <100 ; i++) {
            maxHeap.offer(i*(int)(100*Math.random()));
        }

        for (int i = 0; i < 100; i++) {
            maxHeap.poll();
        }
        System.out.println();
    }

    // PriorityQueue 是一个无界的优先级队列，用堆结构实现
    // 构建一个大顶堆，堆中保存值和对应的位置，每次窗口右移，添加新值后，堆顶的值如果是在窗口左侧，需要剔除
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length < 2) {
            return nums;
        }

        // 新增加的值如果比堆中的值都小，并不会丢失，当堆顶元素poll出后，未在堆中的值会补充进去
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(k, (o1, o2) -> o2[0] - o1[0]);
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            // 堆中保存值和对应的位置
            maxHeap.offer(new int[]{nums[i], i});
        }

        result.add(maxHeap.peek()[0]);
        for (int i = k; i < nums.length; i++) {
            // 添加到堆，如果比前面三个都大，那么就位于堆顶，
            maxHeap.offer(new int[]{nums[i], i});
            // 如果堆顶的最大值是在窗口的左侧，则这个值是无效的，剔除
            while (maxHeap.peek() != null && maxHeap.peek()[1] <= i - k) {
                // 把堆顶的不属于窗口的值去掉，堆中存在的都是窗口内的值，以及窗口左侧小于窗口中最大的值的那部分
                maxHeap.poll();
            }
            // 此时堆顶只剩窗口中的最大值
            result.add(maxHeap.peek()[0]);
        }

        return result.stream().mapToInt(v -> v).toArray();
    }
}
