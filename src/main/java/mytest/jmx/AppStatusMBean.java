package mytest.jmx;

import javax.management.NotificationEmitter;

public interface AppStatusMBean extends NotificationEmitter {
    int getThreadPoolSize();

    void setThreadPoolSize(int size);

    int getUserOnlineNum();

    void setUserOnlineNum(int num);
}
