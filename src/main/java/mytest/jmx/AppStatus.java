package mytest.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class AppStatus extends NotificationBroadcasterSupport implements AppStatusMBean {

    private int threadPoolSize =10;
    private int userOnlineNum =0;
    public static final AppStatus instance = new AppStatus();

    public AppStatus() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName =new ObjectName("com.feiya.test:type=AppStatus");
            mbs.registerMBean(this,objectName);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    @Override
    public void setThreadPoolSize(int size) {
        this.threadPoolSize = size;
    }

    @Override
    public int getUserOnlineNum() {
        return userOnlineNum;
    }

    @Override
    public void setUserOnlineNum(int num) {
        this.userOnlineNum = num;
    }
}
