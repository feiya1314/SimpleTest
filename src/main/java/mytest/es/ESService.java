package mytest.es;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ESService {
    Result createIndex(String indexName, Map<String, Object> mappings, IndexOptions options);

    CompletableFuture<Result> createIndexAsync();

    GetResult matchAll();
    GetResult boolQuery();
    GetResult matchQuery();


}
