package mytest.jmx;

public class JmxTest {
    public static void main(String[] args) {
        AppStatus appStatus = AppStatus.instance;
        System.out.println("start app");
        String jmxPort = System.getProperty("com.sun.management.jmxremote.port");
        System.out.println("jmx port "+jmxPort);
        new Thread(()->{
            try {
                while (true){
                    int i=appStatus.getUserOnlineNum()+1;
                    appStatus.setUserOnlineNum(i);
                    System.out.println("cur user num : "+appStatus.getUserOnlineNum());
                    System.out.println("cur thread num : "+appStatus.getThreadPoolSize());
                    Thread.sleep(20000);
                }

            }catch (Exception e){
                System.out.println("error occurs");
            }
        }).start();
    }
    /*java -cp SimpleTest-1.0-SNAPSHOT.jar -Dcom.sun.management.jmxremote.port=7199 -Dcom.sun.management.jmxremote.rmi.port=7199 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.managemenjmxremote.authenticate=false mytest.jmx.JmxTest*/
}
