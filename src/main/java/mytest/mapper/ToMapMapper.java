package mytest.mapper;

import mytest.DynaSqlProvider.ConfigDynaSqlProvider;
import mytest.ServiceConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.util.List;

public interface ToMapMapper {
    @Select("SELECT configVersion,configs FROM t_config_snapshots WHERE serviceName=#{serviceName} AND clusterName=#{clusterName}")
    List<ServiceConfig> getServiceConfig(@Param("serviceName") String serviceName, @Param("clusterName") String clusterName);

    @Select("SELECT configVersion,configs,username,password FROM t_config_snapshots WHERE serviceName=#{serviceName} AND clusterName=#{clusterName} AND configVersion =#{configVersion}")
    ServiceConfig getSingleServiceConfig(@Param("serviceName") String serviceName, @Param("clusterName") String clusterName, @Param("configVersion") Long configVersion);

    @Select("SELECT configs FROM t_config_snapshots WHERE serviceName=#{serviceName} AND clusterName=#{clusterName} AND configVersion =#{configVersion}")
    String getConfig(@Param("serviceName") String serviceName, @Param("clusterName") String clusterName, @Param("configVersion") Long configVersion);

    @Insert("INSERT INTO t_config_snapshots (serviceName,clusterName,configVersion,configs) VALUES (#{serviceName},#{clusterName},#{configVersion},#{configs})")
    int addConfig(@Param("serviceName") String serviceName, @Param("clusterName") String clusterName, @Param("configVersion") Long configVersion, @Param("configs") String configs);

    //@Insert("INSERT INTO t_config_snapshots (serviceName,clusterName,configVersion,configs,username,password) VALUES (#{serviceName},#{clusterName},#{configVersion},#{configs},#{username},#{password})")
    @InsertProvider(type = ConfigDynaSqlProvider.class,method = "insertServiceConfig")
    int addAllConfig(ServiceConfig serviceConfig);
}
