package mytest.algorithm.array;

/**
 * 数组中的逆序对
 * 在数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对。输入一个数组，求出这个数组中的逆序对的总数。
 * 示例 1:
 * <p>
 * 输入: [7,5,6,4]
 * 输出: 5
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/shu-zu-zhong-de-ni-xu-dui-lcof
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class ReversePairs {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        //int[] arr = new int[]{7, 5, 6, 4};
        //Util.print(arr);
        System.out.println(reversePairsCount(arr, 0, arr.length - 1));
        // Util.print(arr);

    }

    /**
     * 采用归并法去解决。归并时，会进行连个有序数组合并，合并时，左右两个子数组会进行比较，如果左数组的元素大于右数组的一个元素，
     * 那么左数组的这个元素及其后面的所有元素，针对右数组的这个元素，都构成逆序对。
     * 例如{8,12,16,22,100} 和 R = { 9, 26, 55, 64, 91 }  在进行合并时，8 < 9 所以 8 构不成逆序，8取出，开始下一个比较。
     * 12 > 9 构成逆序，12 之后肯定都大于 9，所以，12 之后的也构成逆序对，取出9 ，依次类推。
     *
     * @param arr
     * @param low
     * @param high
     * @return 逆序对的个数
     */
    public static int reversePairsCount(int[] arr, int low, int high) {
        // 小于两个元素，不用比较，此时有 0 个逆序对
        if (high - low < 1) {
            return 0;
        }

        int mid = (low + high) / 2;

        // 递归找到每个子数组的逆序对，然后合并
        int result = reversePairsCount(arr, low, mid) + reversePairsCount(arr, mid + 1, high);

        // 合并数组，如果左侧的数组最大值小于等于右侧数组的最小值，则没有逆序对，也不需要合并，已经是有序的
        if (arr[mid] <= arr[mid + 1]) {
            return result;
        }
        // 构造一个数组，存放合并后的数据
        int[] temp = new int[high - low + 1];
        int i = low;
        int j = mid + 1;
        int k = 0;

        // 合并数组
        while (i <= mid && j <= high) {
            if (arr[i] > arr[j]) {
                temp[k++] = arr[j++];
                // 如果 arr[i] > arr[j] 说明是逆序对，那么 arr[i] - arr[mid] 都可以和 arr[j] 组成逆序对
                result = result + mid - i + 1;
            } else {
                temp[k++] = arr[i++];
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= high) {
            temp[k++] = arr[j++];
        }
        // 将临时数据放入原始数组
        for (int v : temp) {
            arr[low++] = v;
        }
        return result;
    }
}
