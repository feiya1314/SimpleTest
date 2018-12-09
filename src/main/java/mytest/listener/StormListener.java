package mytest.listener;

import mytest.event.StormEvent;
import mytest.event.Weather;
import mytest.event.WeatherEvent;
import org.springframework.context.ApplicationListener;

public class StormListener extends BaseListener{

    @Override
    public void onApplicationEvent(WeatherEvent weatherEvent) {
        Weather weather = weatherEvent.getWeather();
        if (weatherEvent instanceof StormEvent) {
            System.out.println("StormListener roger StormEvent");
            if (weather.getWindLevel()>5){
                System.out.println("rainLevel : " + weather.getRainLevel() + ", windLevel :" + weather.getWindLevel());
                alert("alert big storm is coming ");
            }
        }
    }

    @Override
    void alert(String msg) {
        System.out.println(msg);
    }
}
