package mytest.listener;

import mytest.event.WeatherEvent;
import org.springframework.context.ApplicationListener;

abstract class BaseListener implements ApplicationListener<WeatherEvent> {
    abstract void alert(String msg);
}
