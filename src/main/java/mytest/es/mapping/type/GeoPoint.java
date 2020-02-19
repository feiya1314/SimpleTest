package mytest.es.mapping.type;

public class GeoPoint extends DataType {
    private String latitude;
    private String longitude;
    @Override
    public String getType() {
        return "geo_point";
    }

    /*@Override
    public <P,R> Map getValue(Map p) {

        return <R> ;
    }*/

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
