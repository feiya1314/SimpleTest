package mytest.jmx;

import javax.management.*;

public class AppStatus extends NotificationBroadcasterSupport implements AppStatusMBean {

    private int threadPoolSize =10;
    private int userOnlineNum =0;
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
