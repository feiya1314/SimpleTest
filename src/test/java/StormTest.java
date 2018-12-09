import mytest.event.RainEvent;
import mytest.event.StormEvent;
import mytest.event.Weather;
import org.junit.Test;

public class StormTest extends BaseTest{
    @Test
    public void test(){
        Weather weather = getWeather(0,12,"Storm");
        StormEvent stormEvent = new StormEvent("stormEvent");
        stormEvent.setWeather(weather);
        weatherPublisher.publishEvent(stormEvent);
    }
}
