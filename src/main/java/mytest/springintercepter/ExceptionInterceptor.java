package mytest.springintercepter;



import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class ExceptionInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("exception interceptor start");
        Object result = methodInvocation.proceed();
        System.out.println("exception interceptor start");
        return result;
    }
}
