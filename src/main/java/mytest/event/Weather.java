package mytest.event;

public class Weather {
    private String weather ;
    private int windLevel;
    private int rainLevel;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(int windLevel) {
        this.windLevel = windLevel;
    }

    public int getRainLevel() {
        return rainLevel;
    }

    public void setRainLevel(int rainLevel) {
        this.rainLevel = rainLevel;
    }
}
