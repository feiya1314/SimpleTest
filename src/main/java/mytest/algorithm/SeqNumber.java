package mytest.algorithm;

import java.util.HashSet;

/**
 * 找出最长的连续序列
 * 本题也可以使用动态规划去做
 */
public class SeqNumber {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 12, 34, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        System.out.println(process(arr));
    }

    public static int process(int[] arr) {
        HashSet<Integer> set = new HashSet<>();
        for (int v : arr) {
            set.add(v);
        }
        int count = 0;
        int countTmp = 0;
        int start = 0;

        for (int v : arr) {
            if (set.contains(v - 1)) {
                continue;
            }
            int i = v;
            countTmp++;
            while (set.contains(i + 1)) {
                i++;
                countTmp++;
            }
            if (countTmp > count) {
                count = countTmp;
                countTmp = 0;
                start = v;
            }
        }
        for (int i = 0; i < count; i++) {
            System.out.println(start + i);
        }
        System.out.println();
        return count;
    }
}
