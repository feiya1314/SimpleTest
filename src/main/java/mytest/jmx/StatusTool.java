package mytest.jmx;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StatusTool {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port =7199;
        String username =null;
        String pwd =null;
        String fmtUrl = "service:jmx:rmi:///jndi/rmi://[%s]:%d/jmxrmi";
        String ssObjName = "com.feiya.test:type=AppStatus";
        String cmd=null;
        JMXConnector jmxc;
        AppStatusMBean appStatusMBeanProxy;
        MBeanServerConnection mbeanServerConn;
        System.out.println("status tool start");
        System.out.println("param : "+ Arrays.toString(args));
        if (args.length !=0){
            for (int i = 0; i < args.length; i++) {
                switch (args[i]){
                    case "host":
                        host = args[i+1];
                        i+=1;
                        break;
                    case "username":
                        username = args[i+1];
                        i+=1;
                        break;
                    case "pwd":
                        pwd = args[i+1];
                        i+=1;
                        break;
                    case "cmd":
                        cmd = args[i+1];
                        i+=1;
                        break;
                }
            }
        }
        try {
            System.out.println("host : "+host);
            JMXServiceURL jmxUrl = new JMXServiceURL(String.format(fmtUrl, host, port));
            String ssenable = System.getProperty("ssl.enable");
            System.out.println("ssenable : " +ssenable );
            RMIClientSocketFactory rmiClientSocketFactory = Boolean.parseBoolean(System.getProperty("ssl.enable")) ? new SslRMIClientSocketFactory() : RMISocketFactory.getDefaultSocketFactory();
            Map<String, Object> env = new HashMap<>();
            if (username != null && !username.isEmpty()) {
                String[] creds = {username, pwd};
                env.put(JMXConnector.CREDENTIALS, creds);
            }
            env.put("com.sun.jndi.rmi.factory.socket", rmiClientSocketFactory);

            jmxc = JMXConnectorFactory.connect(jmxUrl, env);
            mbeanServerConn = jmxc.getMBeanServerConnection();
            ObjectName name = new ObjectName(ssObjName);
            appStatusMBeanProxy = JMX.newMBeanProxy(mbeanServerConn, name, AppStatusMBean.class);
            switch (cmd){
                case "getUserNum":
                    System.out.println(String.valueOf(appStatusMBeanProxy.getUserOnlineNum()));
                    break;
                case "setUserNum":
                    appStatusMBeanProxy.setUserOnlineNum(Integer.valueOf(args[args.length-1]));
                    System.out.println("afterSet : "+appStatusMBeanProxy.getUserOnlineNum());
                    break;
                case "getThreadNum":
                    System.out.println(appStatusMBeanProxy.getThreadPoolSize());
                    break;
                case "setThreadNum":
                    appStatusMBeanProxy.setThreadPoolSize(Integer.valueOf(args[args.length-1]));
                    System.out.println("afterSet : "+appStatusMBeanProxy.getThreadPoolSize());
                    break;
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
