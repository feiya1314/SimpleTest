package mytest.config;

public class CommonConfig implements Config {
    private int coreSize;
    private int maxSize;
    private String host;
    private int port;
    private boolean enableSlowQuery;
    private String username;
    private String pwd;

    public int getCoreSize() {
        return coreSize;
    }

    @ConfigurationKey(value = "config.common.coreSize")
    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @ConfigurationKey(value = "config.common.maxSize")
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getHost() {
        return host;
    }

    @ConfigurationKey(value = "config.common.host")
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    @ConfigurationKey(value = "config.common.port")
    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnableSlowQuery() {
        return enableSlowQuery;
    }

    @ConfigurationKey(value = "config.common.enableSlowQuery")
    public void setEnableSlowQuery(boolean enableSlowQuery) {
        this.enableSlowQuery = enableSlowQuery;
    }

    public String getUsername() {
        return username;
    }

    @ConfigurationKey(value = "config.common.username")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    @ConfigurationKey(value = "config.common.pwd", secret = true)
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
