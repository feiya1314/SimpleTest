package mytest.algorithm.tree.heap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给定一个逗号分割的字符串，按照单词出现的频率降序输出，频次相同的，按照字典顺序输出
 * 例如 输入 "Java,C,Js,Python,Js,C,C,Python,Js,Js"
 * 输出 "Js C Python Java"
 *
 * @author : feiya
 * @date : 2021/3/20
 * @description :
 */
public class WordFreq {
    public static void main(String[] args) {
        System.out.println(new WordFreq().solution2("Java,C,Js,Js,C,Python,C,Go,Java,Java,Java,Java"));
    }

    // 首先将计算每个单词出现的次数，并放到map中
    // 然后创建一个map，key是出现频率，value是单词list，存放频率相同的单词
    // 对频率进行排序
    // 遍历排序后的数组，并从map中取出list，对list进行排序，按照字典顺序，然后添加到输出结果中
    public String solution(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }

        String[] strs = input.split(",");
        if (strs.length <= 1) {
            return strs[0];
        }

        Map<String, Integer> strMap = new HashMap<>();
        for (String str : strs) {
            strMap.merge(str, 1, Integer::sum);
        }

        Map<Integer, List<String>> frq = new HashMap<>();
        strMap.forEach((k, v) -> {
            List<String> strings = frq.computeIfAbsent(v, k1 -> new ArrayList<>());
            strings.add(k);
        });

        // mapToInt方法是将Stream中的元素转换成int类型的
        // int[] freK = frq.keySet().stream().mapToInt(k->k).toArray(); 可以使用这句代替下面
        int[] freK = new int[frq.keySet().size()];
        int i = 0;
        for (int k : frq.keySet()) {
            freK[i] = k;
            i++;
        }
        Arrays.sort(freK);

        StringBuilder sb = new StringBuilder();
        int index = freK.length - 1;
        for (int j = index; j >= 0; j--) {
            List<String> strings = frq.get(freK[j]);
            strings.stream().sorted().forEach(s -> sb.append(s).append(" "));
            /*String[] sArr = strings.toArray(new String[0]);
            Arrays.sort(sArr);
            for (String s : sArr) {
                sb.append(s).append(" ");
            }*/
        }

        return sb.toString();
    }

    // 桶排序
    // 把对应字符的频率放入map 创建一个 list数组，数组下标为出现频率的位置，放入list，
    // list中存放频率相同的字符串 遍历数组，有list的位置，把list进行排序，之后输出
    public String solution2(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }

        String[] strs = input.split(",");
        if (strs.length <= 1) {
            return strs[0];
        }
        Map<String, Integer> freqMap = new HashMap<>();
        for (String s : strs) {
            freqMap.merge(s, 1, Integer::sum);
        }
        List<String>[] listArr = new ArrayList[strs.length + 1];
        freqMap.forEach((k, v) -> {
            List<String> list = listArr[v];
            if (list == null) {
                list = new ArrayList<>();
                listArr[v] = list;
            }
            list.add(k);
        });

        StringBuilder sb = new StringBuilder();
        for (int i = listArr.length - 1; i > 0; i--) {
            List<String> list = listArr[i];
            if (list != null) {
                list.stream().sorted().forEach(s -> sb.append(s).append(" "));
            }
        }

        return sb.toString();
    }
}
