package mytest;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class MapToJson {
    public static void main(String[] args) {
        Map<String,String> config = new HashMap<>();
        Map<String,String> toconfig = new HashMap<>();
        config.put("cassandra.contact","10.23.31.45");
        config.put("cassandra.user","cassandra");

        /*String res=JSON.toJSON(config);*/
        System.out.println(JSON.toJSONString(config));
        toconfig=JSON.parseObject(JSON.toJSONString(config),Map.class);
        System.out.println(toconfig);
    }
}
