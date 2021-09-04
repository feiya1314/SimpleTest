package mytest.algorithm.dichotomy;

/**
 * @author : feiya
 * @date : 2021/9/5
 * @description :
 */
public class FindLastNotBiggerTarget {
    public static void main(String[] args) {
        System.out.println(solution2(new int[]{1, 1, 2, 3, 7, 7, 7, 9, 9, 10}, 11));
    }

    /**
     * 从有序数组中查找 不大于某数的（第一个）元素
     * 先找出 不大于某数的（最后一个）元素，然后从这个数字往前推
     * @param source
     * @param target
     * @return
     */
    public static int solution2(int[] source, int target) {
        if (source == null || source.length == 0) {
            return 1;
        }

        int left = 0;
        int right = source.length - 1;
        int last = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (source[mid] <= target) {
                // 如果是最后一个，即mid是数组右边界的值，或者mid右边的值大于target，则mid此时是满足条件的结果，直接返回
                if (mid + 1 >= source.length || source[mid + 1] > target) {
                    // 如果是要返回第几个而不是下标的话，需要 return mid + 1
                    last = mid;
                    break;
                }
                left = mid + 1;
                continue;
            }
            right = mid - 1;
        }

        // 没有小于等于target的值，返回数组长度加1
        if (last < 0) {
            return source.length + 1;
        }

        // 当前小于等于target的最后一个元素已经是 第一个）元素了，直接返回
        if (last == 0 || source[last - 1] < source[last]) {
            return last;
        }
        int tempV = source[last];
        int index = last - 1;
        while (index >= 0) {
            if (source[index] < tempV) {
                return index + 1;
            }
            index--;
        }
        return 0;
    }

    /**
     * 从有序数组中查找 不大于某数的（最后一个）元素
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
            if (source[mid] <= target) {
                // 如果是最后一个，即mid是数组右边界的值，或者mid右边的值大于target，则mid此时是满足条件的结果，直接返回
                if (mid + 1 >= source.length || source[mid + 1] > target) {
                    // 如果是要返回第几个而不是下标的话，需要 return mid + 1
                    return mid;
                }
                left = mid + 1;
                continue;
            }
            right = mid - 1;
        }

        return source.length + 1;
    }
}
