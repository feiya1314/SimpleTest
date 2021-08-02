package mytest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/*
 1 1 2 22 23 23 23

* */
public class T {
    public static void main(String[] args) {
        totalCount();
    }

    public static void totalCount() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Map<Integer, Integer> map = new HashMap<>();
        int result = 0;
        int cur = 0;
        while (cur < n) {
            int val = sc.nextInt();
            Integer count = map.get(val);
            count = count == null ? 1 : (count + 1);
            map.put(val, count);
            cur++;
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            Integer key = entry.getKey();
            Integer val = entry.getValue();
            Integer targetVal = map.get(24 - key);
            if (targetVal ==null){
                it.remove();
                continue;
            }
            result += val * targetVal;
            it.remove();
        }
        System.out.println(result);
    }

    public static String subCollection(String source) {
        StringBuilder stars = new StringBuilder();
        StringBuilder nonStars = new StringBuilder();
        char[] sourceChars = source.toCharArray();
        for (char c : sourceChars) {
            if (c == '*') {
                stars.append("*");
                continue;
            }
            nonStars.append(c);
        }
        return stars.append(nonStars).toString();
    }
}

