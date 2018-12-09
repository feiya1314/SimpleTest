package mytest.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SportProxy implements InvocationHandler{
    private Object target;

    public SportProxy(){
    }

    public SportProxy(Object target){
        this.target=target;
    }
    public Object bind(Object target){
        this.target=target;
        //返回代理对象
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("start to play");
        Object result = null;
        result = method.invoke(target,args);
        System.out.println("invoke result :" );
        System.out.println("play finished");
        return result;
    }
}
