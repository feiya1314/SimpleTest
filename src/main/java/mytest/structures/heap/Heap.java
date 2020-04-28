package mytest.structures.heap;

public class Heap {
    private int[] element;

    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        constructBigTopHeap(arr);
        printHeap(arr);
    }

    public static void constructBigTopHeap(int[] source) {
        int length = source.length;

        // 	最后一个非叶子节点下标 = length/2-1 ,从此处开始
        for (int i = length / 2 - 1; i >= 0; i--) {
            adjustBigTopHeap(source, i, length);
        }

    }

    /**
     * 调整某一父节点及它的所有子节点位置
     *
     * @param arr    原数组
     * @param father 父节点坐标
     * @param length 数组长度
     */
    private static void adjustBigTopHeap(int[] arr, int father, int length) {
        int fVal = arr[father];
        for (int son = (2 * father + 1); son < length; son = son * 2 + 1) { // 从第一个左子节点开始
            if (son + 1 < length && arr[son] < arr[son + 1]) { // 如果左子节点小于右子节点
                son++; // 使用右子节点比较
            }
            if (arr[son] > fVal) {  // 如果子节点比父节点大，将子节点赋给父节点（不用交换）
                arr[father] = arr[son];
                father = son; // 准备下一次比较，位置在子节点
            } else {
                break; // 此处说明父节点已经比子节点大了，由于最开始从最底层比较的，所以下面的可以确定是已排序的
            }
        }
        arr[father] = fVal; // 将最初的父节点放到小于它的子节点的位置（有可能是子节点的子节点，往下比较时第一个大于所有子节点的位置）
    }

    public static void printHeap(int[] source) {
        int temp = 1;

        for (int i = 0; i < source.length; temp++, i = 2 * i + 1) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < source.length && j < Math.pow(2,temp) -1; j++) {
                sb.append(source[j]).append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
