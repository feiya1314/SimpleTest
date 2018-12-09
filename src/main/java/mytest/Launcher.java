package mytest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Launcher {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application-configure.xml", "application-mybatis.xml"});
        context.registerShutdownHook();
        context.refresh();
        TestMapFromJson testMapFromJson=(TestMapFromJson)context.getBean("mapjson");
       // List<ServiceConfig> configs = testMapFromJson.getConf("mytest","yftest");
       // ServiceConfig singleconfigs=testMapFromJson.getSingleConf("mytest","yftest",Long.parseLong("123456789"));
        //Map<String,String> singl=testMapFromJson.getConfig("mytest","yftest",Long.parseLong("123456789"));
        //System.out.println(singl.toString());
        Map<String,String> addcon=new HashMap<>();
        addcon.put("cassandra.conta","1112.222.45");
        addcon.put("cassandra.user","cassandra");
        addcon.put("cassandra.pw","cassandra");
        addcon.put("etcd.enable","true");
        ServiceConfig serviceConfig  = new ServiceConfig();
        serviceConfig.setUsername("yftest");
        serviceConfig.setPassword("yftest");
        serviceConfig.setClusterName("test");
        serviceConfig.setServiceName("test");
        serviceConfig.setConfigs(addcon);
        serviceConfig.setConfigVersion(System.currentTimeMillis());
        testMapFromJson.addAllConfig(serviceConfig);
    }
}
