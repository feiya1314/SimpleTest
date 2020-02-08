package mytest.future;

public class InstantNoodles {
    private String box = "complete";
    private String noodles = "full";
    private Enum status = Status.ORIGINAL;

    public void openShell() {
        box = "opened";
        System.out.println("open box");
    }

    public void prepare() {
        System.out.println("准备泡面");
        System.out.println("拆泡面");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        System.out.println("放调料");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }

    public void cook(String water) {
        System.out.println("cooking noodles");
        System.out.println("加开水开始泡面 ： " + water);
        status = Status.COOKING;
        try {
            Thread.sleep(20000);
        } catch (Exception e) {

        }
        System.out.println("noodles done");
        status = Status.DONE;
    }

    public boolean done() {
        return status == Status.DONE;
    }

    public String getBox() {
        return box;
    }

    public String getNoodles() {
        if (done()){
            return "cooked noodles, eat please";
        }
        return "noodles not been cooked , can not eat";
    }

    private enum Status {
        ORIGINAL, COOKING, DONE
    }
}
