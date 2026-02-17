package mytest.algorithm.slidingwindow;

import java.util.*;

/**
 * 给定一个包含 n 个整数的数组 nums 和一个目标值 target，判断 nums 中是否存在四个元素 a，b，c 和 d ，使得 a + b + c +
 * // d 的值与 target 相等？找出所有满足条件且不重复的四元组。
 * //
 * // 注意：
 * //
 * // 答案中不可以包含重复的四元组。
 * //
 * // 示例：
 * //
 * // 给定数组 nums = [1, 0, -1, 0, -2, 2]，和 target = 0。
 * //
 * //满足要求的四元组集合为：
 * //[
 * //  [-1,  0, 0, 1],
 * //  [-2, -1, 1, 2],
 * //  [-2,  0, 0, 2]
 * //]
 * //
 * // Related Topics 数组 哈希表 双指针
 * <p>
 * https://leetcode-cn.com/problems/4sum/
 *
 * @author : yufei
 * @date : 2021/1/14 19:19
 * @description :
 */
public class FourSum {

    boolean[] used;

    public static void main(String[] args) {
        System.out.println(new FourSum().fourSum2(new int[]{-493, -482, -482, -456, -427, -405, -392, -385, -351, -269, -259, -251, -235, -235, -202, -201, -194, -189, -187, -186, -180, -177, -175, -156, -150, -147, -140, -122, -112, -112, -105, -98, -49, -38, -35, -34, -18, 20, 52, 53, 57, 76, 124, 126, 128, 132, 142, 147, 157, 180, 207, 227, 274, 296, 311, 334, 336, 337, 339, 349, 354, 363, 372, 378, 383, 413, 431, 471, 474, 481, 492}, 6189));
        // System.out.println(new FourSum().fourSum2(new int[]{1, 0, -1, 0, -2, 2},0));
        System.out.println("re");
    }

    // 双指针，排序，先固定前面两个数， i 和 j，其中 i<j 。后面两个数分别从j+1 和 n-1(尾部)，每次计算四个数之和
    // 如果和等于target，则将4个数加入结果，并且左指针右移，直到遇到不同的数，并且右指针左移，直到遇到不同的数，（因为有序数组）
    // 如果和大于target，则右指针左移（有序数组，左指针右移，肯定也是大于target）
    // 如果和小于target，则左指针右移动
    // 双指针这里复杂度为n，而暴力解法，这个复杂度为n^2

    public static List<List<Integer>> fourSum0(int[] nums, int target) {
        if (nums == null || nums.length < 4) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 3; i++) {
            // 重复
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            // 最小的四个都比target大，没必要计算了
            if ((long) nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target) {
                break;
            }

            // i开始加上最后的三个，仍然小于target，则中间的肯定也小于target，没必要继续
            if ((long) nums[i] + nums[nums.length - 1] + nums[nums.length - 2] + nums[nums.length - 3] < target) {
                continue;
            }

            for (int j = i + 1; j < nums.length - 2; j++) {
                // 重复的跳过
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    continue;
                }

                // j开始和后面两个相加已经大于target了，也不用再计算了
                if ((long) nums[i] + nums[j + 1] + nums[j + 2] + nums[j] > target) {
                    break;
                }

                // i ,j 加上最后的2个，仍然小于target，则中间的肯定也小于target，没必要继续
                if ((long) nums[i] + nums[j] + nums[nums.length - 1] + nums[nums.length - 2] < target) {
                    continue;
                }

                int l = j + 1;
                int r = nums.length - 1;
                while (l < r) {
                    // 左指针重复，跳过
                    if (l > j + 1 && nums[l] == nums[l - 1]) {
                        l++;
                        continue;
                    }
                    // 右指针重复，跳过
                    if (r < nums.length - 1 && nums[r] == nums[r + 1]) {
                        r--;
                        continue;
                    }
                    // 整型溢出
                    long sum = (long) nums[i] + nums[j] + nums[l] + nums[r];
                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        l++;
                        r--;
                        continue;
                    }

                    if (sum < target) {
                        l++;
                    } else {
                        r--;
                    }
                }
            }
        }
        return result;
    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        if (nums == null || nums.length < 4) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i - 1] == nums[i]) {
                continue;
            }
            // i开始的连续三个都大于target，则后面肯定也大于，没必要计算
            if (nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target) {
                break;
            }

            // i开始加上最后的三个，仍然小于target，则中间的肯定也小于target，没必要继续
            if (nums[i] + nums[nums.length - 1] + nums[nums.length - 2] + nums[nums.length - 3] < target) {
                continue;
            }

            for (int j = i + 1; j < nums.length - 2; j++) {
                // 和前一个数相等则跳过，属于重复的
                if (j > i + 1 && nums[j - 1] == nums[j]) {
                    continue;
                }
                // j开始的连续三个都大于target，则后面肯定也大于，没必要计算
                if (nums[i] + nums[j] + nums[j + 1] + nums[j + 2] > target) {
                    break;
                }
                // i ,j 加上最后的2个，仍然小于target，则中间的肯定也小于target，没必要继续
                if (nums[i] + nums[j] + nums[nums.length - 1] + nums[nums.length - 2] < target) {
                    continue;
                }
                int left = j + 1;
                int right = nums.length - 1;
                while (left < right) {
                    int sum=nums[i]+nums[j]+nums[left]+nums[right];
                    // 相等，两指针向中间靠拢，可能有其他相等的组合
                    if (sum==target){
                        result.add(Arrays.asList(nums[i],nums[j],nums[left],nums[right]));
                        left++;
                        while (nums[left]==nums[left-1] && left<right){
                            left++;
                        }
                        right--;
                        while (nums[right+1] == nums[right] && left<right){
                            right--;
                        }
                    }else if (sum<target){
                        left++;
                        while (nums[left]==nums[left-1] && left<right){
                            left++;
                        }
                    }else {
                        right--;
                        while (nums[right+1] == nums[right] && left<right){
                            right--;
                        }
                    }
                }
            }
        }
        return result;
    }

    // 四重循环，暴力解法，先固定前面两个数，然后另外遍历剩余的两个数，先排序，遇到重复的跳过
    public List<List<Integer>> fourSum3(int[] nums, int target) {
        if (nums == null || nums.length < 4) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        int left = 0;
        int preLeft = 0;
        while (left < nums.length - 3) {
            if (left > 0 && nums[left] == preLeft) {
                left++; // 这里加上，不然会死循环
                continue;
            }
            int preRight = 0;
            int initIndex = left + 1;
            for (int right = left + 1; right < nums.length - 2; right++) {
                if (right > initIndex && nums[right] == preRight) {
                    continue;
                }
                int preILeft = 0;
                int indexIL = right + 1;
                for (int iLeft = right + 1; iLeft < nums.length - 1; iLeft++) {
                    if (iLeft > indexIL && nums[iLeft] == preILeft) {
                        continue;
                    }
                    int preIRight = 0;
                    int indexIR = iLeft + 1;
                    for (int iRight = iLeft + 1; iRight < nums.length; iRight++) {
                        if (iRight > indexIR && nums[iRight] == preIRight) {
                            continue;
                        }
                        if ((nums[left] + nums[right] + nums[iLeft] + nums[iRight]) == target) {
                            List<Integer> line = new ArrayList<>();
                            line.add(nums[left]);
                            line.add(nums[right]);
                            line.add(nums[iLeft]);
                            line.add(nums[iRight]);
                            result.add(line);
                        }
                        preIRight = nums[iRight];
                    }
                    preILeft = nums[iLeft];
                }
                preRight = nums[right];
            }
            preLeft = nums[left];
            left++;
        }
        return result;
    }

    // 使用backtrack 回溯算法,剪枝不够会超时
    public List<List<Integer>> fourSum2(int[] nums, int target) {
        if (nums == null || nums.length < 4) {
            return Collections.emptyList();
        }
        Arrays.sort(nums);
        used = new boolean[nums.length];
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(nums, result, 0, target, track);

        return result;
    }

    private void backtrack(int[] nums, List<List<Integer>> result, int start, int target, LinkedList<Integer> track) {
        if (track.size() == 4 && track.stream().mapToInt(i -> i).sum() == target) {
            result.add(new ArrayList<>(track));
            return;
        }

        for (int i = start; i < nums.length; i++) {
            if (used[i] || (i>start && nums[i] == nums[i-1])) {
                continue;
            }
            track.add(nums[i]);
            used[i] = true;
            backtrack(nums, result, i + 1, target, track);
            used[i] = false;
            track.removeLast();
        }
    }
}
