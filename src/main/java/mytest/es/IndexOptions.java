package mytest.es;

import org.elasticsearch.action.admin.indices.alias.Alias;

public class IndexOptions {
    private Alias alias;
    private int shardsNum =5;
    private int replicasNum=2;
    private int waitForActiveShards;
    private int timeout;
    private int masterTimeout;

    public Alias getAlias() {
        return alias;
    }

    public IndexOptions setAlias(Alias alias) {
        this.alias = alias;
        return this;
    }

    public int getShardsNum() {
        return shardsNum;
    }

    public IndexOptions setShardsNum(int shardsNum) {
        this.shardsNum = shardsNum;
        return this;
    }

    public int getReplicasNum() {
        return replicasNum;
    }

    public IndexOptions setReplicasNum(int replicasNum) {
        this.replicasNum = replicasNum;
        return this;
    }
}
