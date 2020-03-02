package mytest.config;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

public class ConfigTest {
    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        File configFile = getFile("config.properties");
        properties.load(new FileReader(configFile));

        CommonConfig commonConfig = new CommonConfig();
        convertValue(properties, commonConfig);

    }

    public  static File getFile(String file){
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

    public static void convertValue(Properties properties, Config config) {
        Class<? extends Config> clz = config.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            ConfigurationKey configurationKey = method.getAnnotation(ConfigurationKey.class);
            if (configurationKey == null){continue;}
            String configKey = configurationKey.value();
            String value = properties.getProperty(configKey);

            boolean secret = configurationKey.secret();
            if (secret){
                value += " decrypted";
            }
            method.setAccessible(true);

            System.out.println("key:" + configKey + " ;secret : "+ secret + " ;value : "+value);
        }

    }
}
