package mytest.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodSetter extends ConfigSetter {
    private Method method;
    private Config target;

    public MethodSetter(Method method, Config target) {
        this.method = method;
        this.target = target;
    }

    @Override
    protected void setValue(String value) {
        Type type = method.getGenericParameterTypes()[0];
        Object realV = convertClass((Class) type, value);

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            method.invoke(target, realV);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
