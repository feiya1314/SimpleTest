package mytest.es;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mappings {
    private XContentBuilder builder;
    private Map<String, Object> mapping = new HashMap<>();
    private Map<String, Property> properties = new HashMap<>();

    public Property property(String propertyName) {
        Property property = new Property(propertyName);
        properties.put(propertyName, property);

        return property;
    }

    public Map<String, Object> build() {
        Map<String, Object> fields = new HashMap<>();
        properties.forEach((fieldName, pro) -> {
            fields.put(fieldName, pro.getFieldDetail());
        });
        mapping.put("properties", fields);

        return mapping;
    }

    public static void main(String[] args) {
        Mappings mappings = new Mappings();

        mappings.property("message").textType().analyzer("my_analyzer");
        mappings.property("title").geoPointType().analyzer("title_analyzer").boost("2.0");

        String json = JSON.toJSONString(mappings.build());
        System.out.println(json);
    }

    public Mappings builder() throws IOException {
        builder = XContentFactory.jsonBuilder();
        builder.startObject();
        return this;
    }

    public static class Property {
        private String fieldName;
        private String fieldType;
        private Map<String, Object> fieldDetail = new HashMap<>();

        private Property(String fieldName) {
            this.fieldName = fieldName;
        }

        private Property(String fieldName, String fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        private Map<String, Object> getFieldDetail() {
            fieldDetail.put("type", this.fieldType);
            return fieldDetail;
        }

        public Property fieldType(String type) {
            this.fieldType = type;
            return this;
        }
        public Property textType() {
            this.fieldType = "text";
            return this;
        }

        public Property geoPointType() {
            this.fieldType = "geo_point";
            return this;
        }

        public Property analyzer(String analyzerName) {
            fieldDetail.put("analyzer", analyzerName);
            return this;
        }

        public Property boost(String boostValue) {
            fieldDetail.put("boost", boostValue);
            return this;
        }

        public Property multiFields(Map<String, Object> multiField) {
            fieldDetail.put("fields", multiField);
            return this;
        }

    }

    private class type {

    }
}
