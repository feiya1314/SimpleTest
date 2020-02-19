package mytest.es.mapping.type;

public class IP extends Text {
    @Override
    public String getType() {
        return "ip";
    }
}
