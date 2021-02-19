package mytest.algorithm.slidingwindow;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定两个字符串 s1 和 s2，写一个函数来判断 s2 是否包含 s1 的排列。
 * <p>
 * 换句话说，第一个字符串的排列之一是第二个字符串的子串。
 * <p>
 * 示例1:
 * <p>
 * 输入: s1 = "ab" s2 = "eidbaooo"
 * 输出: True
 * 解释: s2 包含 s1 的排列之一 ("ba").
 *  
 * <p>
 * 示例2:
 * <p>
 * 输入: s1= "ab" s2 = "eidboaoo"
 * 输出: False
 *  
 * <p>
 * 注意：
 * <p>
 * 输入的字符串只包含小写字母
 * 两个字符串的长度都在 [1, 10,000] 之间
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/permutation-in-string
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/10
 * @description :
 */
public class CheckInclusion {
    public static void main(String[] args) {
        System.out.println(new CheckInclusion().checkInclusion("ab", "eidbaooo"));
    }

    // 每次取s1长度的子串比较
    public boolean checkInclusion(String s1, String s2) {
        char[] s1c = s1.toCharArray();
        char[] s2c = s2.toCharArray();
        Map<Character, Integer> s1Map = new HashMap<>();
        Map<Character, Integer> curMap = new HashMap<>();
        for (char c : s1c) {
            s1Map.merge(c, 1, Integer::sum);
        }
        int left = 0;
        int right = 0;
        while (left < s2c.length - s1c.length + 1) {
            for (right = left; right < left + s1c.length; right++) {
                if (!s1Map.containsKey(s2c[right])) {
                    break;
                }
                /*int count = curMap.getOrDefault(s2c[right],0);
                if (count>=s1Map.get(s2c[right])){
                    right
                }*/
                curMap.merge(s2c[right], 1, Integer::sum);
            }
            if (sameMap(s1Map, curMap)) {
                return true;
            }
            curMap.clear();
            left++;
        }
        return false;
    }

    // 滑动窗口 ,窗口先从0增长，然后左右开始向右滑动
    public boolean checkInclusion2(String s1, String s2) {
        char[] s1c = s1.toCharArray();
        char[] s2c = s2.toCharArray();
        Map<Character, Integer> s1Map = new HashMap<>();
        Map<Character, Integer> curMap = new HashMap<>();
        for (char c : s1c) {
            s1Map.merge(c, 1, Integer::sum);
        }
        int left = 0;
        int right = 0;
        while (right < s2c.length) {
            curMap.merge(s2c[right], 1, Integer::sum);
            if (right - left + 1 < s1c.length) {
                right++;
                continue;
            }

            if (sameMap(s1Map, curMap)) {
                return true;
            }
            // 最左边的字符对应的curMap中count-1，如果为0则curMap中删除这个字符
            curMap.merge(s2c[left], 1, (oldValue, newValue) -> {
                int n = oldValue - 1;
                if (n == 0) {
                    return null;
                }
                return n;
            });
            left++;
            right++;
        }
        return false;
    }

    private boolean sameMap(Map<Character, Integer> s1Map, Map<Character, Integer> curMap) {
        if (s1Map.size() != curMap.size()) {
            return false;
        }
        for (Map.Entry<Character, Integer> e : s1Map.entrySet()) {
            if (!e.getValue().equals(curMap.get(e.getKey()))) {
                return false;
            }
        }

        return true;
    }
}
