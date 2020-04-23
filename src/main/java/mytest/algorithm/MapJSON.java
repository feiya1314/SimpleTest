package mytest.algorithm;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapJSON {
    public static void main(String[] args) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("1", "bar");
        m.put("2", "foo.bar");
        m.put("3", "foo.foo");
        m.put("4", "baz.cloudmall.com");
        m.put("5", "baz.cloudmall.ai");

        System.out.println(CloudmallInterview2(m));
    }

    public static String CloudmallInterview2(Map<String, String> RevList) {
        Iterator<Map.Entry<String, String>> iterator = RevList.entrySet().iterator();
        Map<String, Object> result = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            String[] values = value.split("\\.");
            getMostInsideMap(values, result, key);
        }
        System.out.println(result);
        return JSON.toJSONString(result);
    }

    // baz.cloudmall.ai
    public static Map<String, Object> getMostInsideMap(String[] realKeys, Map<String, Object> map, String realValue) {
        if (realKeys.length < 2) {
            map.put(realKeys[0], realValue);
            return map;
        }

        for (String key : realKeys) {
            Map m = (Map) map.get(key);
            if (m == null) {
                m = new HashMap<String, Object>();
                map.put(key, m);
            }
            String[] sub = Arrays.copyOfRange(realKeys, 1, realKeys.length);
            return getMostInsideMap(sub, m, realValue);

        }
        return map;
    }
}
