package mytest.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsClientFactory {
    private TransportClient transportClient;
    private RestHighLevelClient restHighLevelClient;
    private Settings settings;
    private ElasticSearchConfig esConfig;

    public EsClientFactory(ElasticSearchConfig esConfig){
        this.esConfig=esConfig;
    }
    public TransportClient getTransportClient(){
        if (transportClient == null){
            int seedsNum=esConfig.getSeeds().length;
            TransportAddress[] transportAddress = new TransportAddress[seedsNum];

            for (int i = 0; i <esConfig.getSeeds().length; i++) {
                try {
                    transportAddress[i] = new TransportAddress(InetAddress.getByName(esConfig.getSeeds()[i]),esConfig.getPort());
                }catch (UnknownHostException e){
                    throw new ESConnectException("UnknownHostException");
                }
            }
            settings = Settings.builder().put("cluster.name",esConfig.getClusterName())
                   // .put("sniff",esConfig.getSniffer())
                   // .put("test","yetest")
                    .build();
            transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddresses(transportAddress);
        }
        return transportClient;
    }

    public RestHighLevelClient getRestHighLevelClient(){
        if (restHighLevelClient == null){
            int seedsNum=esConfig.getSeeds().length;
            HttpHost[] httpHosts= new HttpHost[seedsNum];
            for (int i = 0; i <esConfig.getSeeds().length; i++) {
                httpHosts[i] = new HttpHost(esConfig.getSeeds()[i],esConfig.getHttpPort(),esConfig.getHttpProtocol());
            }
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(httpHosts)
            );
        }
        return restHighLevelClient;
    }

    public ElasticSearchConfig getEsConfig() {
        return esConfig;
    }

    public void setEsConfig(ElasticSearchConfig esConfig) {
        this.esConfig = esConfig;
    }
}
