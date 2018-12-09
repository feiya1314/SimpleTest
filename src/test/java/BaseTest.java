import mytest.aop.InterceptorTest;
import mytest.es.EsClientFactory;
import mytest.event.Weather;
import mytest.event.WeatherPublisher;
import mytest.listener.RainListener;
import mytest.listener.StormListener;
import mytest.listener.WeatherListener;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-configure-test.xml",
        "classpath:application-mybatis-test.xml"})
public class BaseTest {
    @Autowired
    WeatherPublisher weatherPublisher;
    @Autowired
    WeatherListener weatherListener;
    @Autowired
    RainListener rainListener;
    @Autowired
    StormListener stormListener;
    @Autowired
    InterceptorTest interceptorTestProxy;
    @Autowired
    EsClientFactory esClientFactory;

    public Weather getWeather(int rainLevel,int windLevel,String sunOrRain){
        Weather weather = new Weather();
        weather.setRainLevel(rainLevel);
        weather.setWindLevel(windLevel);
        weather.setWeather(sunOrRain);
        return weather;
    }
}
