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
        int[] arr = new int[]{1, 2, 4, 2, 4,5,6,5,7,1,1};
        int[] result = getTwoSum(arr,13);
        System.out.println("a : " + result[0] + " b : " + result[1]);
    }

    /*
     * 解法分析：
     *
     *
     *
     */
    public static int[] getTwoSum(int[] arrays, int target){
        int arrLength = arrays.length;
        int[] result = new int[2];
        Map<Integer,Integer> numMap = new HashMap<>(arrLength);

        for (int i = 0; i <arrLength ; i++) {
            numMap.put(arrays[i],i);
        }

        for (int i = 0; i <arrLength ; i++) {
            int another = target - arrays[i];
            Integer anotherInMap = numMap.get(another);
            if (anotherInMap!=null && anotherInMap !=i){
                result[0] = i;
                result[1] = anotherInMap;
            }
        }
        return result;
    }
}
