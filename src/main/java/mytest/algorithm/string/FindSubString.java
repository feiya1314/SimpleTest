package mytest.algorithm.string;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * 30. 串联所有单词的子串
 * 给定一个字符串 s 和一些长度相同的单词 words。找出 s 中恰好可以由 words 中所有单词串联形成的子串的起始位置,
 * 即便有重复的也得加上。
 * <p>
 * 注意子串要与 words 中的单词完全匹配，中间不能有其他字符，但不需要考虑 words 中单词串联的顺序。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：
 * s = "barfoothefoobarman",
 * words = ["foo","bar"]
 * 输出：[0,9]
 * 解释：
 * 从索引 0 和 9 开始的子串分别是 "barfoo" 和 "foobar" 。
 * 输出的顺序不重要, [9,0] 也是有效答案。
 * 示例 2：
 * <p>
 * 输入：
 * s = "wordgoodgoodgoodbestword",
 * words = ["word","good","best","word"]
 * 输出：[]
 * https://leetcode-cn.com/problems/substring-with-concatenation-of-all-words/
 * @author : yufei
 * @date : 2020/12/17 19:24
 * @description :
 */
public class FindSubString {
    public static void main(String[] args) {
        System.out.println(findSubString("barfoothefoobarman", new String[]{"foo", "bar"}));
    }

    /**
     * 整体思路 words 单词放入map，遍历s，每次取 单词长度，比较是否在map中，并放入map，最后和原map比较，数量是否一致
     *
     * @param s
     * @param words
     * @return
     */
    public static List<Integer> findSubString(String s, String[] words) {
        if (words.length < 1) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();
        int wordLen = words[0].length();
        int wordsLen = words.length;
        int allWordLen = wordLen * wordsLen;
        Map<String, Integer> wordsMap = new HashMap<>();
        // words 放入map v记录单词出现次数
        for (String word : words) {
            wordsMap.merge(word, 1, Integer::sum);
        }
        Map<String, Integer> curMap = new HashMap<>();
        // 双指针 每次从 i开始取单词
        for (int i = 0; i < s.length() - allWordLen + 1; i++) {
            for (int j = 1; j <= wordsLen; j++) {
                String cur = s.substring(i + j * wordLen - wordLen, i + j * wordLen);
                // 不包含单词，说明i开头的单词不符合
                if ((!wordsMap.containsKey(cur)) || curMap.getOrDefault(cur, 0) > wordsMap.get(cur)) {
                    break;
                }
                // 放入map
                curMap.merge(cur, 1, Integer::sum);
            }
            // 比较数量是否匹配
            boolean allMatch = true;
            for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
                if (!entry.getValue().equals(curMap.get(entry.getKey()))) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                result.add(i);
            }
            curMap.clear();
        }
        return result;
    }
}
