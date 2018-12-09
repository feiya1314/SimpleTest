package mytest.interceptor;

import mytest.MD5Util;
import mytest.ServiceConfig;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.xml.transform.Result;
import java.util.Properties;
@Intercepts({@Signature(type= Executor.class,method = "update",args = {MappedStatement.class,Object.class}),
             @Signature(type= Executor.class,method = "query",args = {MappedStatement.class,Object.class, RowBounds.class,ResultHandler.class})})
public class PwdInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getArgs()[1];
        Object result=null;
        if (target instanceof ServiceConfig){
            ServiceConfig serviceConfig = (ServiceConfig)target;
            String pwd = serviceConfig.getPassword();
            serviceConfig.setPassword(MD5Util.getSaltMD5(pwd));
            result=invocation.proceed();
            serviceConfig.setPassword(pwd);
        }
        return result==null?invocation.proceed():result;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
