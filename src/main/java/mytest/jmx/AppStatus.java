package mytest.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.atomic.AtomicLong;

public class AppStatus extends NotificationBroadcasterSupport implements AppStatusMBean {

    private int threadPoolSize =10;
    private int userOnlineNum =0;
    private final AtomicLong notificationSerialNumber = new AtomicLong();
    public static final AppStatus instance = new AppStatus();
    ObjectName objectName=null;

    public AppStatus() {
        //LocateRegistry.createRegistry(7199);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            objectName =new ObjectName("com.feiya.test:type=AppStatus");
            mbs.registerMBean(this,objectName);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getThreadPoolSize() {
        sendNotify("getThreadPoolSize");
        return threadPoolSize;
    }

    @Override
    public void setThreadPoolSize(int size) {
        sendNotify("setThreadPoolSize");
        this.threadPoolSize = size;
    }

    @Override
    public int getUserOnlineNum() {
        sendNotify("getUserOnlineNum");
        return userOnlineNum;
    }

    @Override
    public void setUserOnlineNum(int num) {
        sendNotify("setUserOnlineNum");
        this.userOnlineNum = num;
    }

    private void sendNotify(String msg)
    {
        Notification jmxNotification = new Notification("check", objectName, notificationSerialNumber.incrementAndGet(), msg);
        int[] userData = new int[2];
        userData[0]= userOnlineNum;
        userData[1]= threadPoolSize;
        jmxNotification.setUserData(userData);
        sendNotification(jmxNotification);
    }
}
