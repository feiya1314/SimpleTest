package mytest.es;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;

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
    public Result createIndex(String indexName, Map<String,Object> mappings, IndexOptions options) {
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
        return null;
    }
}
