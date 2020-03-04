package mytest.config;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class ConfigSetter {
    protected Object convertClass(Class type, String value) {
        if (type.equals(String.class)) {
            return value;
        }

        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(value);
        }

        if (type.isAnnotation()) {
            String[] valueArr = StringUtils.split(value, ",");
            int length = valueArr.length;
            Class componentType = type.getComponentType();
            Object result = Array.newInstance(componentType, length);

            for (int i = 0; i < length; i++) {
                Array.set(result, i, convertClass(componentType, valueArr[i]));
            }
            return result;
        }
        return null;
    }

    protected Object convertParameterizedType(ParameterizedType type, String value) throws ClassNotFoundException {
        Type t = type.getRawType();
        Type argument = type.getActualTypeArguments()[0];
        Collection finalValue;
        if (t.equals(List.class)) {
            finalValue = new ArrayList();
        } else if (t.equals(Set.class)) {
            finalValue = new HashSet();
        } else {
            return Class.forName(t.getTypeName());
        }

        String[] valueArr = StringUtils.split(value, ",");

        for (String ss : valueArr) {
            Object obj = convertClass((Class) argument, ss);
            finalValue.add(obj);
        }

        return finalValue;
    }

    public void convertMethodConfig(Properties properties, Config config, Decryption decryption) {
        Class<? extends Config> clz = config.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            ConfigurationKey configurationKey = method.getAnnotation(ConfigurationKey.class);
            if (configurationKey == null) {
                continue;
            }
            String configKey = configurationKey.value();
            String value = properties.getProperty(configKey);

            boolean secret = configurationKey.secret();
            if (secret) {
                value = decryption.decrypt(value);
            }

            new MethodSetter(method, config).setValue(value);
        }
    }

    public void convertFieldConfig(Properties properties, Config config, Decryption decryption) {
        Class<? extends Config> clz = config.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            ConfigurationKey configurationKey = field.getAnnotation(ConfigurationKey.class);
            if (configurationKey == null) {
                continue;
            }
            String configKey = configurationKey.value();
            String value = properties.getProperty(configKey);

            boolean secret = configurationKey.secret();
            if (secret) {
                value = decryption.decrypt(value);
            }
            new FieldSetter(field, config).setValue(value);
        }
    }

    protected abstract void setValue(String value);
}
