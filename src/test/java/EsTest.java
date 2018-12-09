import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

public class EsTest extends BaseTest {
    @Test
    public void highLevelClientIndicesTest() throws IOException {
        RestHighLevelClient restHighLevelClient = esClientFactory.getRestHighLevelClient();

        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices("indexbyhighlevel");
        if (!restHighLevelClient.indices().exists(getIndexRequest)) {
            System.out.println("index indexbyhighlevel is not exist,now create it");
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("indexbyhighlevel");
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 1));
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest);
            System.out.println(createIndexResponse.index());
        } else {
            System.out.println("index indexbyhighlevel is exist");
        }

    }

    @Test
    public void tranClientIndicesTest() {
        TransportClient client = esClientFactory.getTransportClient();
        IndicesAdminClient indicesAdminClient = client.admin().indices();

        IndicesExistsRequest request = new IndicesExistsRequest("testindex");
        IndicesExistsResponse response = indicesAdminClient.exists(request).actionGet();
        if (response.isExists()) {
            System.out.println("index testindex is exist");
            //indicesAdminClient.prepareDelete("testindex").get();
        } else {
            System.out.println("index testindex is not exist,create it");
            indicesAdminClient.prepareCreate("testindex").setSettings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 1)
            ).get();
        }
    }

    @Test
    public void highLevelClientGetTest() throws IOException{
        RestHighLevelClient restHighLevelClient = esClientFactory.getRestHighLevelClient();
        GetRequest getRequest = new GetRequest("indexcreatedbybuild");
        getRequest.type("usertype").id("2");
        GetResponse getResponse = restHighLevelClient.get(getRequest);
        System.out.println(getResponse.getSourceAsString());
    }

    @Test
    public void highLevelClientSearchTest() throws IOException{
        RestHighLevelClient restHighLevelClient = esClientFactory.getRestHighLevelClient();
        SearchRequest searchRequest = new SearchRequest("indexcreatedbybuild");
        searchRequest.types("usertype");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder mqb = QueryBuilders.matchQuery("msg","你好李四");

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.query(mqb);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits searchHits = searchResponse.getHits();
        System.out.println(searchHits.totalHits);
        Iterator<SearchHit> iterator =searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit=iterator.next();
            System.out.println(searchHit.getSourceAsString());
        }
    }

    @Test
    public void tranClientGetTest() {
        TransportClient client = esClientFactory.getTransportClient();
        GetResponse getResponse = client.prepareGet().setIndex("indexcreatedbybuild").setType("usertype").setId("2").get();
        System.out.println(getResponse.getSource().toString());
    }

    @Test
    public void tranClientSeatchTest() {
        TransportClient client = esClientFactory.getTransportClient();
        MatchQueryBuilder mqb = QueryBuilders.matchQuery("msg","你好李四");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.query(mqb);

        //SearchResponse searchResponse = client.prepareSearch("indexcreatedbybuild").setTypes("usertype").setQuery(mqb).get();
        //上面或者下面都可以
        SearchResponse searchResponse = client.prepareSearch("indexcreatedbybuild").setTypes("usertype").setSource(searchSourceBuilder).get();

        SearchHits searchHits = searchResponse.getHits();
        System.out.println(searchHits.totalHits);
        Iterator<SearchHit> iterator =searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit=iterator.next();
            System.out.println(searchHit.getSourceAsString());
        }
    }

    @Test
    public void highLevelClientDeleteTest() {

    }
}
