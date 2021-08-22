package mytest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : feiya
 * @date : 2021/8/14
 * @description :
 */
public class ReviewCode {
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        Map<Integer, Integer> integerMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int another = target - nums[i];
            Integer index = integerMap.get(another);
            if (index != null) {
                result[0] = i;
                result[1] = index;
                return result;
            }

            integerMap.put(nums[i], i);
        }

        return result;
    }
}
