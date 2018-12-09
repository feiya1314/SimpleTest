package mytest.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class WeatherPublisher implements ApplicationEventPublisherAware{
    private ApplicationEventPublisher applicationEventPublisher;
    private WeatherPublisher(){};

    public void publishEvent(WeatherEvent weatherEvent){
        applicationEventPublisher.publishEvent(weatherEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) throws BeansException {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
