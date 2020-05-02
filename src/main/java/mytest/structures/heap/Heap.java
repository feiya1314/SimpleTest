package mytest.structures.heap;

public class Heap {

    public static void main(String[] args) {

        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        constructBigTopHeap(arr, arr.length);
        printHeap(arr);
        System.out.println("----------------------------------------------------");
        constructSmallTopHeap(arr, arr.length);
        printHeap(arr);
        // printAsTree(arr,arr.length);
    }

    public static void constructBigTopHeap(int[] source, int length) {
        // 	最后一个非叶子节点下标 = length/2-1 ,从此处开始
        for (int i = length / 2 - 1; i >= 0; i--) {
            adjustBigTopHeap(source, i, length);
        }

    }

    public static void constructSmallTopHeap(int[] source, int length) {
        // 	最后一个非叶子节点下标 = length/2-1 ,从此处开始
        for (int i = length / 2 - 1; i >= 0; i--) {
            adjustSmallTopHeap(source, i, length);
        }

    }

    private static void adjustSmallTopHeap(int[] arr, int father, int length) {
        int fVal = arr[father];
        for (int son = (2 * father + 1); son < length; son = (2 * son + 1)) {
            if (son + 1 < length && arr[son + 1] < arr[son]) {
                son++;
            }

            if (arr[son] < fVal) {  // 不能使用 arr[father] 因为 father 会改变, 而且是为了给 fVal 找正确位置
                arr[father] = arr[son];
                father = son;
            } else {
                break;
            }
        }
        arr[father] = fVal;
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
        for (int son = (2 * father + 1); son < length; son = son * 2 + 1) { // 从第一个左子节点开始，然后下一次去比较子节点的左子节点
            if (son + 1 < length && arr[son] < arr[son + 1]) { // 如果左子节点小于右子节点
                son++; // 使用右子节点比较
            }
            // 找到比左右子节点最大的位置就是目标位置
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
            for (int j = i; j < source.length && j < (1 << temp) - 1; j++) {
                sb.append(source[j]).append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    public static void printAsTree(int[] data, int size) {
        int lineNum = 1;//首先遍历第一行
        int lines = (int) (Math.log(size) / Math.log(2)) + 1;//lines是堆的层数
        int spaceNum = (int) (Math.pow(2, lines) - 1);
        for (int i = 1; i <= size; ) { //因为在[1...size]左闭右闭区间存数据，data[0]不存数据

            //每层都是打印这个区间[2^(层数-1) ... (2^层数)-1]。如果堆里的数不够(2^层数)-1个，那就打印到size。所以取min((2^层数)-1,size).
            for (int j = (int) Math.pow(2, lineNum - 1); j <= Math.min(size, (int) Math.pow(2, lineNum) - 1); j++) {
                // if (j>=size) break;
                printSpace(spaceNum); //打印spaceNum个空格
                System.out.printf("%3s", data[j]);//打印数据
                System.out.printf("%3s", "");//图片中绿色方框
                printSpace(spaceNum);//打印spaceNum个空格
                i++;//每打印一个元素就 + 1
            }
            lineNum++;
            spaceNum = spaceNum / 2;
            System.out.println();
        }
    }

    public static void printSpace(int n) {//打印n个空格(在这里用‘\t’来代替)
        for (int i = 0; i < n; i++) {
            System.out.printf("%3s", "");
        }
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
