package mytest.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;

public class SecondDumpString {

    public static void main(String[] args) {
        System.out.println(solution(" word word hello hello", 2));
    }
    public static String solution(String words, int n) {
        String[] strings = words.split(" ");
        LinkedHashMap<String, Integer> freqMap = new LinkedHashMap<>();

        for (String s : strings) {
            if (freqMap.containsKey(s)) {
                freqMap.put(s, freqMap.get(s) + 1);
            } else {
                freqMap.put(s, 1);
            }
        }

        String result = "not found";
        int index = 2;
        for (Map.Entry<String, Integer> ent : freqMap.entrySet()) {
            if (ent.getValue() == n && (index - 1 == 0)) {
                return ent.getKey();
            } else {
                index--;
            }
        }

        return result;
    }
}
