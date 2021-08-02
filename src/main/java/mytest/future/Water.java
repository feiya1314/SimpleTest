package mytest.future;

public class Water {
    private int temperature = 20;
    private int status = 0;

    public void heating()  {
        System.out.println("开始烧水");
        for (int i = 0; i < 4; i++) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            temperature += 20;
            if (temperature > 60) {
                System.out.println("主动烧水异常");
                throw new RuntimeException("error heat");
            }
            System.out.println("水温上升20°C ,当前为 ： " + temperature);
            if (temperature >= 100) {
                status = 1;
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
