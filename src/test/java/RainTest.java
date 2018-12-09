import mytest.event.RainEvent;
import mytest.event.Weather;
import org.junit.Test;

public class RainTest extends BaseTest{
    @Test
    public void test(){
        Weather weather = getWeather(8,6,"rain");
        RainEvent rainEvent = new RainEvent("rainEvent");
        rainEvent.setWeather(weather);
        weatherPublisher.publishEvent(rainEvent);
    }
}
