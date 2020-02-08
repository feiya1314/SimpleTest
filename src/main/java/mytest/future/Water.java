package mytest.future;

public class Water {
    private int temperature = 20;
    private int status = 0;

    public void heating() {
        System.out.println("开始烧水");
        for (int i = 0; i < 4; i++) {
            try {
                Thread.sleep(5000);
                temperature += 20;
                System.out.println("水温上升20°C ,当前为 ： " + temperature);
                if (temperature >= 100) {
                    status = 1;
                }
            } catch (Exception e) {

            }
        }
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean available() {
        return status == 1;
    }

    public String getWater() {
        if (!available()) {
            return null;
        }
        return "BoiledWater";
    }
}
