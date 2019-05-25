package mytest.algorithm;

import java.util.HashMap;
import java.util.Map;

/*
 * 问题：给定一个数组，找出两个数，使其之和等于给定的数字，返回其所在数组的序号
 * 假设只有唯一解
 * 例如 numbers=[2, 7, 11, 15], target=9 返回 [0, 1]
 * */
public class TwoSum {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 4, 2, 4, 5, 6, 5, 7, 1, 1, 1, 2, 3, 4, 1, 2};
        int[] result = getTwoSum(arr, 13);
        System.out.println("a : " + result[0] + " b : " + result[1]);
        result = getTwoSum2(arr, 13);
        System.out.println("a : " + result[0] + " b : " + result[1]);
    }

    /*
     * 解法分析：
     * 将数组放入map 数组的值作为key  序号作为value，再遍历数组，用target - arr[i] 得到另一个值，
     * 然后判断另一个值是否再map中，注意，要判断一下 序号不能是当前的，避免结果两个值相同时，返回了同一个序号
     *
     */
    public static int[] getTwoSum(int[] arrays, int target) {
        int arrLength = arrays.length;
        int[] result = new int[2];
        Map<Integer, Integer> numMap = new HashMap<>(arrLength);
        int times = 0;
        for (int i = 0; i < arrLength; i++) {
            numMap.put(arrays[i], i);
            times++;
        }

        for (int i = 0; i < arrLength; i++) {
            int another = target - arrays[i];
            Integer anotherInMap = numMap.get(another);
            if (anotherInMap != null && anotherInMap != i) {
                result[0] = i;
                result[1] = anotherInMap;
                break;
            }
            times++;
        }
        System.out.println("getTwoSum times : " + times);
        return result;
    }

    /*
     * 比上面少遍历一次，放入map时就开始检查,如果找到了第一个值，此时第二个值还未遍历到，map中不会有第二值的信息，
     * 会继续遍历，找到第二个值时，第一个值的信息已经再map了，即可找到，后续就不再遍历,比第一种方法更好一点
     *
     **/

    public static int[] getTwoSum2(int[] arrays, int target) {
        int arrLength = arrays.length;
        int[] result = new int[2];
        Map<Integer, Integer> numMap = new HashMap<>(arrLength);
        int times = 0;
        for (int i = 0; i < arrLength; i++) {
            times++;
            numMap.put(arrays[i], i);
            int another = target - arrays[i];
            Integer anotherInMap = numMap.get(another);
            if (anotherInMap != null && anotherInMap != i) {
                result[0] = i;
                result[1] = anotherInMap;
                break;
            }
        }
        System.out.println("getTwoSum2 times : " + times);
        return result;
    }
}
