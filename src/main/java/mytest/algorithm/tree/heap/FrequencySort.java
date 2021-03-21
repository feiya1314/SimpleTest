package mytest.algorithm.tree.heap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 给定一个字符串，请将字符串里的字符按照出现的频率降序排列。
 * <p>
 * 示例 1:
 * <p>
 * 输入:
 * "tree"
 * <p>
 * 输出:
 * "eert"
 * <p>
 * 解释:
 * 'e'出现两次，'r'和't'都只出现一次。
 * 因此'e'必须出现在'r'和't'之前。此外，"eetr"也是一个有效的答案。
 * 示例 2:
 * <p>
 * 输入:
 * "cccaaa"
 * <p>
 * 输出:
 * "cccaaa"
 * <p>
 * 解释:
 * 'c'和'a'都出现三次。此外，"aaaccc"也是有效的答案。
 * 注意"cacaca"是不正确的，因为相同的字母必须放在一起。
 * 示例 3:
 * <p>
 * 输入:
 * "Aabb"
 * <p>
 * 输出:
 * "bbAa"
 * <p>
 * 解释:
 * 此外，"bbaA"也是一个有效的答案，但"Aabb"是不正确的。
 * 注意'A'和'a'被认为是两种不同的字符。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sort-characters-by-frequency
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/3/20
 * @description :
 */
public class FrequencySort {
    public static void main(String[] args) {
        System.out.println('a' + 0);
        System.out.println('z' + 0);
        System.out.println('A' + 0);
        System.out.println('Z' + 0);

    }

    // 桶排序
    // 首先把每个单词的频率找到，放到map
    // 然后创建一个List数组，数组长度为 s+1，数组的下标作为字符出现的频率，比如出现频率为 3的字符，
    // 放到数组下标为 3 的位置，数组中存放的是List引用，频率相同的字符放到list中
    // 由于数组中只有出现频率的下标位置有数据，其他都没有，比如没有频率为 4 的字符，那么，下标 4 的地方就没有数据，以空间换时间
    public String frequencySort(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }

        Map<Character, Integer> freqM = new HashMap<>();
        for (char c : s.toCharArray()) {
            // 等同于 freqM.put(c, freqM.getOrDefault(c, 0) + 1);
            freqM.merge(c, 1, Integer::sum);
        }

        List<Character>[] buckets = new ArrayList[s.length() + 1];
        for (char k : freqM.keySet()) {
            int v = freqM.get(k);
            List arr = buckets[v];
            if (arr == null) {
                arr = new ArrayList<Character>();
                buckets[v] = arr;
            }
            arr.add(k);
        }
        StringBuilder sb = new StringBuilder();

        for (int index = buckets.length - 1; index > 0; index--) {
            if (buckets[index] != null) {
                // 这里list中字符是无序的，需要
                List<Character> list = buckets[index];
                for (char c : list) {
                    for (int i = 0; i < index; i++) {
                        sb.append(c);
                    }
                }
            }
        }

        return sb.toString();
    }

    // A-Z 65-90，a-z 97-122
    // 使用大顶堆，将字符对应的 ASCII 码作为数组下标，数组中存放字符出现频率（数组有些位置会是空，没有用到）
    // 然后将数组中频率不为0的对应的字符串添加到 大顶堆 中，以频率作为比较
    // 最后每次取出堆顶元素，并从数组中得到该字符出现的频率，添加到字符串中
    public String frequencySort2(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        int[] chars = new int[123];
        // 字符的整数值对应数组的位置，例如 a对应65，那么数组65处存放a出现的频率
        for (char c : s.toCharArray()) {
            chars[c] = chars[c] + 1;
        }

        // PriorityQueue offer方法，每次增加元素，都要保证堆序。
        // poll方法会返回队首元素（堆顶），并将元素从堆中删除。每次弹出队首元素，都要进行堆调整。同时堆 size-1
        // 容量为123的大顶堆 （a b -> a-b 是小顶堆，b-a是大顶堆），大顶堆的比较对像是char数组中对应索引位置的值，比较的是频率
        PriorityQueue<Character> heap = new PriorityQueue<>(123, (a, b) -> chars[b] - chars[a]);
        // 将数组中频率大于0的字符添加到heap中
        for (int j = 0; j < 123; j++) {
            if (chars[j] > 0) {
                heap.offer((char) j);
            }
        }

        StringBuilder sb = new StringBuilder();
        while (!heap.isEmpty()) {
            // 堆顶是频率最大字符，取出堆顶元素
            char c = heap.poll();
            for (int i = 0; i < chars[c]; i++) {
                sb.append(c);
            }
        }
        //Character.
        return sb.toString();
    }
}
