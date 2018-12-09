package mytest.es;

public class ElasticSearchConfig {
    private String clusterName;
    private String[] seeds;
    private boolean sniffer=true;
    private int port=9300;
    private String httpProtocol = "http";
    private int ping_schedule = 5;
    private int httpPort=9200;

    public ElasticSearchConfig(){};
    public ElasticSearchConfig(String clusterName){
        this.clusterName=clusterName;
    };

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String[] getSeeds() {
        return seeds;
    }

    public void setSeeds(String[] seeds) {
        this.seeds = seeds;
    }

    public boolean getSniffer() {
        return sniffer;
    }

    public void setSniffer(boolean sniffer) {
        this.sniffer = sniffer;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public int getPing_schedule() {
        return ping_schedule;
    }

    public void setPing_schedule(int ping_schedule) {
        this.ping_schedule = ping_schedule;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }
}
