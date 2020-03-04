package mytest.config;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldSetter extends ConfigSetter {
    private Field field;
    private Config target;

    public FieldSetter(Field field, Config target) {
        this.field = field;
        this.target = target;
    }

    @Override
    protected void setValue(String value) {
        Type type = field.getGenericType();
        Object realValue = convertClass((Class) type,value);
        if (!field.isAccessible()){
            field.setAccessible(true);
        }

        try {
            field.set(target, realValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
