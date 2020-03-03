package mytest.config;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ConfigTest {
    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        File configFile = getFile("config.properties");
        properties.load(new FileReader(configFile));

        CommonConfig commonConfig = new CommonConfig();
        convertValue(properties, commonConfig, s -> s + "decrypt");

    }

    public static File getFile(String file) {
        ClassLoader classLoader = new ConfigTest().getClass().getClassLoader();
        /**
         getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
         */
        URL url = classLoader.getResource(file);
        /**
         * url.getFile() 得到这个文件的绝对路径
         */
        return new File(url.getFile());
    }

    public static void convertValue(Properties properties, Config config, Decryption decryption) throws Exception {
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

            Type type = method.getGenericParameterTypes()[0];
            if (type instanceof Class) {
                Object realV = getRealValue((Class) type, value);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(config, realV);
            } else if (type instanceof ParameterizedType) {

            }

            System.out.println("key:" + configKey + " ;secret : " + secret + " ;value : " + value);
        }
    }

    public static Object getRealCollectionValue(ParameterizedType type, String value) {
        Type t = type.getRawType();
        Collection temp = null;
        Collection finalValue = null;
        if (t.equals(List.class)) {
            Type argument = type.getActualTypeArguments()[0];
            String[] valueArr = StringUtils.split(value, ",");
            temp = new ArrayList();
            for (String ss : valueArr) {
                Object obj = getRealValue((Class) argument, value);
                temp.add(obj);
            }

        } else if (t.equals(Set.class)) {

        }
        finalValue.addAll(temp);
        return finalValue;

    }

    public static Object getRealValue(Class clz, String value) {

        if (clz.equals(String.class)) {
            return value;
        }
        if (clz.equals(boolean.class) || clz.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        if (clz.equals(Integer.class) || clz.equals(int.class)) {
            return Integer.parseInt(value);
        }
        return null;
    }
}
