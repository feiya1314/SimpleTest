package mytest.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 三个线程交替打印
 *
 * @author : yufei
 * @date : 2020/12/30 19:36
 * @description :
 */
public class PrintInRoundWith3Thread {
    public static void main(String[] args) {
        String s = "[{\"I\":10241,\"V\":\"6\"},{\"I\":6691,\"V\":\"36.115166\"},{\"I\":6692,\"V\":\"114.310209\"},{\"I\":10791,\"V\":\"1096|1099\"},{\"I\":5753,\"V\":\"北京天赐良园果蔬食品厂|原来\"},{\"I\":5754,\"V\":\"1476\"},{\"I\":5755,\"V\":\"1471\"},{\"I\":5353,\"V\":\"若干\"},{\"I\":5354,\"V\":\"面议-面议\"},{\"I\":5356,\"V\":\"1\"},{\"I\":5357,\"V\":\"1\"},{\"I\":5358,\"V\":\"0\"},{\"I\":5362,\"V\":\"359417429883015168\"},{\"I\":5363,\"V\":\"277\"},{\"I\":11535,\"V\":\"0\"},{\"I\":9011,\"V\":\"19544482126343\"},{\"I\":5461,\"V\":\"hangyeyaoqiu=%E5%A8%B1%E4%B9%90%E4%BC%91%E9%97%B2%2F%E9%A4%90%E9%A5%AE%2F%E6%9C%8D%E5%8A%A1&amp;gongsixingzhi=%E7%A7%81%E8%90%A5&amp;gongzuonianxian=%E4%B8%8D%E9%99%90&amp;xueliyaoqiu=%E4%B8%8D%E9%99%90&amp;gongsiguimo=1-49%E4%BA%BA\"},{\"I\":7510,\"V\":\"8437959\"},{\"I\":7109,\"V\":\"114.30361295576\"},{\"I\":7110,\"V\":\"36.109505702126\"},{\"I\":9184,\"V\":\"2019-08-02 14:45:33\"},{\"I\":10589,\"V\":\"29000014\"},{\"I\":10922,\"V\":\"\"},{\"I\":8992,\"V\":\"29000014\"},{\"I\":111566,\"V\":\"\"},{\"I\":112181,\"V\":\"\"},{\"I\":111249,\"V\":\"23369|23370|36392|36775|37019|37223|60084|400000\"},{\"I\":111247,\"V\":\"11286|22640|6992\"},{\"I\":111235,\"V\":\"4|501\"},{\"I\":111255,\"V\":\"0|157\"},{\"I\":9773,\"V\":\"20190417102528\"},{\"I\":112679,\"V\":\"6668103468\"},{\"I\":112772,\"V\":\"0\"},{\"I\":112682,\"V\":\"59077\"}]";

        JSONArray jsonArray = JSON.parseArray(s);
        Optional<String[]> b = jsonArray.stream().map(obj->(JSONObject) obj).filter(jsonObject -> (int)jsonObject.get("I") == 111249)
                .map(jsonObject -> jsonObject.get("V").toString().split("|")).findFirst();
        //b.ifPresent(strings -> Arrays.stream(strings).collect(Collectors.toSet()).forEach());

        jsonArray.forEach(obj -> {
            JSONObject j = (JSONObject) obj;
            if ((int) j.get("I") == 111249) {
                String value = (String) j.get("V");
                String[] strings = value.split("|");
                for (String str : strings) {
                    //str.equals()
                }
            }
            //System.out.println(v);
        });

        System.out.println(b);
    }
}
