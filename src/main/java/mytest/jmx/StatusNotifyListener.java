package mytest.jmx;

import javax.management.Notification;
import javax.management.NotificationListener;

public class StatusNotifyListener implements NotificationListener {
    @Override
    public void handleNotification(Notification notification, Object handback) {
        if ("check".equals(notification.getType())){
            System.out.println("notify msg : "+notification.getMessage());
            int[] userDate = (int[])notification.getUserData();
            System.out.println("userNum : " + userDate[0]);
            System.out.println("poolSize : " + userDate[1]);
        }
    }
}
