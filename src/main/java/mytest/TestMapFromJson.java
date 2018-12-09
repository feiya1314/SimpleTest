package mytest;

import com.alibaba.fastjson.JSON;
import mytest.mapper.ToMapMapper;
import org.springframework.beans.factory.annotation.Autowired;


import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMapFromJson {
    private static final String CONFIGS_COLUMN = "configs";
    private static final String CONFIGS_CONFIGVersion  = "configVersion";

    @Autowired
    private ToMapMapper toMapMapper;

    public List<ServiceConfig> getConf(String serviceName,String clusterName){
        List<ServiceConfig> configs=toMapMapper.getServiceConfig(serviceName,clusterName);

        List<ServiceConfig> singleconfigs=new ArrayList<>();

        return configs;
    }

    private ServiceConfig mapToConfig(Map<String,String> map)
    {
        ServiceConfig serviceConfig=new ServiceConfig();
        for (Map.Entry<String,String> entry : map.entrySet())
        {
            if(CONFIGS_COLUMN.equals(entry.getKey())) {
                serviceConfig.setConfigs(JSON.parseObject(entry.getValue(),Map.class));
            }
            if (CONFIGS_CONFIGVersion.equals(entry.getKey())){
                //String value11=entry.getValue().toString();
                Long ll=Long.valueOf(entry.getValue());
                serviceConfig.setConfigVersion(ll);
            }
        }
        return  serviceConfig;
    }
    public ServiceConfig getSingleConf(String serviceName,String clusterName,Long congiversion){
        //List<ServiceConfig> configs=toMapMapper.getServiceConfig(serviceName,clusterName);
        ServiceConfig singleconfigs=toMapMapper.getSingleServiceConfig(serviceName,clusterName,congiversion);
        return singleconfigs;
    }

    public Map<String,String> getConfig(String serviceName, String clusterName, Long congiversion){
        //List<ServiceConfig> configs=toMapMapper.getServiceConfig(serviceName,clusterName);
        String singleconfigs=toMapMapper.getConfig(serviceName,clusterName,congiversion);

        Map<String,String> singleconfig= JSON.parseObject(singleconfigs,Map.class);
        return singleconfig;
    }

    public int addConfig(String serviceName, String clusterName, Long congiversion,String config){
        //List<ServiceConfig> configs=toMapMapper.getServiceConfig(serviceName,clusterName);
        int singleconfigs=toMapMapper.addConfig(serviceName,clusterName,congiversion,config);
        return singleconfigs;
    }

    public int addAllConfig(ServiceConfig serviceConfig){
        //List<ServiceConfig> configs=toMapMapper.getServiceConfig(serviceName,clusterName);
        int singleconfigs=toMapMapper.addAllConfig(serviceConfig);
        return singleconfigs;
    }
}
