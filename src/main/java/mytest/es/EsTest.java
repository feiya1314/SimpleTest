package mytest.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

public class EsTest {
    public static void main(String[] args) throws IOException {
        ElasticSearchConfig config = new ElasticSearchConfig();
        config.setSeeds(new String[]{"192.168.3.8","192.168.3.9"});
        EsClientFactory clientFactory = new EsClientFactory(config);
        clientFactory.setKeyStore("client-keystore.jks");
        clientFactory.setKeyStorePwd("234BOPatu");
        clientFactory.setTrustStore("truststore.jks");
        clientFactory.setTrustStorePwd("123BAUjil");

        RestHighLevelClient restHighLevelClient = clientFactory.getRestHighLevelClient();

        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices("testssl");
        /*if (!restHighLevelClient.indices().exists(getIndexRequest())) {
            System.out.println("index testssl is not exist,now create it");
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("tes10tssl");
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 1));
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest);
            System.out.println(createIndexResponse.index());
        } else {
            System.out.println("index indexbyhighlevel is exist");
        }*/

    }
}
