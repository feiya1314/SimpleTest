package mytest.algorithm.string;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 49. 字母异位词分组
 * 给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。
 * <p>
 * 示例:
 * <p>
 * 输入: ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出:
 * [
 * ["ate","eat","tea"],
 * ["nat","tan"],
 * ["bat"]
 * ]
 * 说明：
 * <p>
 * 所有输入均为小写字母。
 * 不考虑答案输出的顺序。
 * 通过次数152,298提交次数
 * https://leetcode-cn.com/problems/group-anagrams/
 * @author : yufei
 * @date : 2020/12/18 16:54
 * @description :
 */
public class SameCharsSameGroup {
    /**
     * 记录每个单词的字符出现次数，次数相同的则为一组
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        if (strs.length < 1) {
            return Collections.emptyList();
        }

        List<List<String>> result = new ArrayList<>();
        return result;
    }

    /**
     * 排序后比较是否相同
     */
    public List<List<String>> groupAnagrams2(String[] strs) {
        Map<String, List<String>> listMap = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);
            List<String> list = listMap.getOrDefault(sorted, new ArrayList<>());
            list.add(s);
            listMap.put(sorted, list);
        }
        return new ArrayList<>(listMap.values());

       /* return new ArrayList<>(Arrays.stream(strs)
                .collect(Collectors.groupingBy(str -> {
                    // 返回 str 排序后的结果。
                    // 按排序后的结果来grouping by，算子类似于 sql 里的 group by。
                    char[] array = str.toCharArray();
                    Arrays.sort(array);
                    return new String(array);
                })).values());*/
    }
}
