package mytest.listener;

import mytest.event.RainEvent;
import mytest.event.Weather;
import mytest.event.WeatherEvent;
import org.springframework.context.ApplicationListener;

public class RainListener extends BaseListener{
    @Override
    public void onApplicationEvent(WeatherEvent weatherEvent) {
        Weather weather = weatherEvent.getWeather();

        if (weatherEvent instanceof RainEvent){
            System.out.println("RainListener roger RainEvent");

            if (weather.getRainLevel()>5){
                System.out.println("rainLevel : " + weather.getRainLevel() + ", windLevel :" + weather.getWindLevel());
                alert("alert big rain is coming ");
            }
        }
    }
    @Override
    void alert(String msg) {
        System.out.println(msg);
    }
}
