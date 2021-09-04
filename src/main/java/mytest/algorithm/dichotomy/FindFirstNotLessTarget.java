package mytest.algorithm.dichotomy;

/**
 * 从有序数组中查找不小于（不大于）某数的第一个（最后一个）元素
 * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
 * <p>
 * https://www.nowcoder.com/questionTerminal/7bc4a1c7c371425d9faa9d1b511fe193
 *
 * @author : feiya
 * @date : 2021/9/4
 * @description :
 */
public class FindFirstNotLessTarget {
    public static void main(String[] args) {
        System.out.println(solution(new int[]{1,1,2,3,7,7,7,9,9,10}, 2));
    }

    /**
     * 请实现有重复数字的升序数组的二分查找。
     * 输出在数组中第一个大于等于查找值的位置，如果数组中不存在这样的数(指不存在大于等于查找值的数)，则输出数组长度加一。
     *
     * @param source
     * @param target
     * @return
     */
    public static int solution(int[] source, int target) {
        if (source == null || source.length == 0) {
            return 1;
        }

        int left = 0;
        int right = source.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (source[mid] >= target) {
                if (mid - 1 < 0 || source[mid - 1] < target) {
                    // 如果是要返回第几个而不是下标的话，需要 return mid + 1
                    return mid;
                }
                right = mid - 1;
                continue;
            }

            left = mid + 1;
        }

        return source.length + 1;
    }
}
