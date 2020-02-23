package mytest.es;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DefaultESService implements ESService {
    private RestHighLevelClient client;

    public DefaultESService(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public GetResult matchQuery() {
        //QueryBuilders.
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("fieldname", "jack");
        matchQueryBuilder.analyzer("search_analyzer");
        matchQueryBuilder.operator(Operator.AND).fuzziness(Fuzziness.AUTO);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GetResult boolQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("username", "jack"));
        queryBuilder.filter(QueryBuilders.termQuery("message", "hello"));
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GetResult constantScoreQuery(float boost) {
        ConstantScoreQueryBuilder scoreQueryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.matchQuery("test","testvalue"));
        scoreQueryBuilder.boost(boost);
        String qu = scoreQueryBuilder.toString();
        System.out.println(qu);
        return null;
    }

    public static void main(String[] args) {
        new DefaultESService(null).constantScoreQuery(2.0f);
    }
    @Override
    public Result createIndex(String indexName, Map<String, Object> mappings, IndexOptions options) {
        Optional<IndexOptions> indexOptions = Optional.ofNullable(options);
        options = indexOptions.orElse(new IndexOptions());

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        CreateIndexResponse response = null;

        request.settings(getSettingsBuilder(options));
        indexOptions.ifPresent((value) -> {
            if (value.getAlias() != null) {
                request.alias(value.getAlias());
            }
        });

        request.mapping(mappings);
        //new Alias("")
        try {
            response = client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {

        }
        response.isFragment();
        return null;
    }

    private Settings.Builder getSettingsBuilder(IndexOptions indexOptions) {
        Settings.Builder builder = Settings.builder();
        builder.put("index.number_of_shards", indexOptions.getShardsNum());
        builder.put("index.number_of_replicas", indexOptions.getReplicasNum());

        return builder;
    }

    @Override
    public CompletableFuture<Result> createIndexAsync() {
        //client.indices().createAsync();
        return null;
    }

    @Override
    public GetResult matchAll() {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
