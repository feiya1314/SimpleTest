package mytest.DynaSqlProvider;

import mytest.ServiceConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class ConfigDynaSqlProvider {
    public String insertServiceConfig(ServiceConfig serviceConfig){
        return new SQL(){
            {
                INSERT_INTO("t_config_snapshots");
                if (StringUtils.isEmpty(serviceConfig.getUsername())){
                    serviceConfig.setUsername("default");
                }
                VALUES("username","#{username}");
                if (!StringUtils.isEmpty(serviceConfig.getServiceName())){
                    VALUES("serviceName","#{serviceName}");
                }
                if (!StringUtils.isEmpty(serviceConfig.getClusterName())){
                    VALUES("clusterName","#{clusterName}");
                }
                if (!StringUtils.isEmpty(serviceConfig.getPassword())){
                    VALUES("password","#{password}");
                }
                VALUES("configVersion","#{configVersion}");
                if (serviceConfig.getConfigs()!=null &&! serviceConfig.getConfigs().isEmpty()){
                    VALUES("configs","#{configs}");
                }
            }
        }.toString();
    }
}
