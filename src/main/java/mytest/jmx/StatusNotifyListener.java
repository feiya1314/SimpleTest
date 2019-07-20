package mytest.jmx;

import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.concurrent.locks.LockSupport;

public class StatusNotifyListener implements NotificationListener {
    private Thread mainThread;
    public StatusNotifyListener(Thread mainThread){
        this.mainThread = mainThread;
    }
    @Override
    public void handleNotification(Notification notification, Object handback) {
        if ("check".equals(notification.getType())){
            System.out.println("notify msg : "+notification.getMessage());
            int[] userDate = (int[])notification.getUserData();
            System.out.println("userNum : " + userDate[0]);
            System.out.println("poolSize : " + userDate[1]);
            LockSupport.unpark(mainThread);
        }
    }
}
