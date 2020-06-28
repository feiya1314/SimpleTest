package mytest.algorithm.tree.bfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 给定两个单词（beginWord 和 endWord）和一个字典，找到从 beginWord 到 endWord 的最短转换序列的长度。转换需遵循如下规则：
 * <p>
 * 每次转换只能改变一个字母。
 * 转换过程中的中间单词必须是字典中的单词。
 * 说明:
 * <p>
 * 如果不存在这样的转换序列，返回 0。
 * 所有单词具有相同的长度。
 * 所有单词只由小写字母组成。
 * 字典中不存在重复的单词。
 * 你可以假设 beginWord 和 endWord 是非空的，且二者不相同。
 * 示例 1:
 * <p>
 * 输入:
 * beginWord = "hit",
 * endWord = "cog",
 * wordList = ["hot","dot","dog","lot","log","cog"]
 * <p>
 * 输出: 5
 * <p>
 * 解释: 一个最短转换序列是 "hit" -> "hot" -> "dot" -> "dog" -> "cog",
 * 返回它的长度 5。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/word-ladder
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class WordChain {

    // 优化，使用双向队列,从头尾开始遍历，相遇时，把两个走的长度相加
    public int ladderLength2(String beginWord, String endWord, List<String> wordList) {
        return 0;
    }

    // 构建图，邻接表
    private Map<String, List<String>> constructGraph(String beginWord, List<String> wordList) {
        Map<String, List<String>> graph = new HashMap<>();
        if (wordList.isEmpty()) {
            return graph;
        }

        wordList.add(beginWord);
        for (String word : wordList) {
            List<String> list = graph.computeIfAbsent(word, k -> new ArrayList<>());
            for (String str : wordList) {
                if (near(word, str)) {
                    list.add(str);
                }
            }
        }

        return graph;
    }

    // 使用邻接表
    public int ladderLength3(String beginWord, String endWord, List<String> wordList) {
        if (wordList.isEmpty() || !wordList.contains(endWord)) {
            return 0;
        }
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        Map<String, List<String>> graph = constructGraph(beginWord, wordList);

        queue.offer(beginWord);
        visited.add(beginWord);

        int count = 0;
        while (!queue.isEmpty()) {
            int curSize = queue.size();
            count++;
            for (int i = 0; i < curSize; i++) {
                String curWord = queue.poll();
                List<String> neighbor = graph.get(curWord);
                for (String tar : neighbor) {
                    if (visited.contains(tar)) {
                        continue;
                    }

                    if (tar.equals(endWord)) {
                        return count + 1;
                    }

                    visited.add(tar);
                    queue.offer(tar);
                }
            }
        }
        return 0;
    }

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (wordList.isEmpty() || !wordList.contains(endWord)) {
            return 0;
        }
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(beginWord);
        visited.add(beginWord);

        int count = 0;

        while (!queue.isEmpty()) {
            int curSize = queue.size(); // 记录当前的需要遍历的只有一个字符不同字符串的数目
            count++; // 新一轮的遍历

            // 每一轮要遍历所有只有一个字符不同字符串
            for (int i = 0; i < curSize; i++) {
                String cur = queue.poll();
                for (String dic : wordList) {
                    if (visited.contains(dic)) {
                        continue;
                    }

                    if (!near(cur, dic)) {
                        continue;
                    }

                    if (dic.equals(endWord)) {
                        return count + 1;  // 找到了相同的之后，需要加一，注意，不能用 count++
                    }
                    visited.add(dic); // 已访问的添加到已访问列表
                    queue.offer(dic);
                }
            }
        }

        return 0;
    }

    private boolean near(String origin, String target) {
        if (origin.equals(target)) {
            return false;
        }

        int length = origin.length();
        boolean hasDiff = false;
        for (int i = 0; i < length; i++) {
            if (origin.charAt(i) != target.charAt(i)) {
                if (hasDiff) {
                    return false;
                }
                hasDiff = true;
            }
        }

        return hasDiff;
    }
}
