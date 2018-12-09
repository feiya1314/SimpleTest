package mytest.listener;

import mytest.event.RainEvent;
import mytest.event.StormEvent;
import mytest.event.WeatherEvent;
import org.springframework.context.ApplicationListener;

public class WeatherListener implements ApplicationListener<WeatherEvent> {
    @Override
    public void onApplicationEvent(WeatherEvent weatherEvent) {
        //System.out.println("WeatherListener roger WeatherEvent");
        if (weatherEvent instanceof RainEvent){
            System.out.println("WeatherListener roger WeatherEvent ,it is RainEvent");
            return ;
        }
        if (weatherEvent instanceof StormEvent){
            System.out.println("WeatherListener roger WeatherEvent ,it is StormEvent");
            return ;
        }
    }
}
