package mytest.es.mapping.type;

public class Text extends DataType {
    private String value;

    @Override
    public String getType() {
        return "text";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
