package mytest.thread;

public class ThreadLocalSession {
    private int id;
    private String name;
    private String status;
    private boolean destroy = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDestroy(){
        return destroy;
    }
    public void destroy(){
        this.destroy = true;
        this.status = "destroyed";
    }

    @Override
    public String toString() {
        return "id : "+id+" name : "+name+" status : " + status;
    }
}
