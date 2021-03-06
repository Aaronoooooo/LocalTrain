package com.elasticresthigh.createadd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author pengfeisu
 * @create 2020-04-14 9:51
 */
public class ElasticCurdclient {
    private RestHighLevelClient client;
    private String ES_INDEX = "posts";
    private String ES_INDEX_TYPE = "doc";
    private String ES_INDEX_ID = "1";
    private static Logger log = LoggerFactory.getLogger(ElasticCurdclient.class);

    public ElasticCurdclient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("106.52.33.54", 9200, "http")));
    }

    public void closeClient() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ElasticCurdclient esClientUtil = new ElasticCurdclient();
//        esClientUtil.listIndex();
//        esClientUtil.bulk2();
//        esClientUtil.search();
        esClientUtil.searchIndex();
//        esClientUtil.createMapIndex();
//        esClientUtil.closeClient();
//        esClientUtil.createJsonStringIndex();
    }

    /*listIndex????????????*/
    public void listIndex() {
        GetRequest getRequest = new GetRequest(
                ES_INDEX,
                ES_INDEX_TYPE,
                ES_INDEX_ID);

        boolean exists = false;
        try {
            exists = client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exists) {
            System.out.println(ES_INDEX + "????????????");
        } else {
            System.out.println(ES_INDEX + "???????????????");

        }

        /*???????????????????????????????????????*/
        getRequest.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);

        /*???????????????"message", "*Date"*/
        String[] includes = new String[]{"message", "*Date"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext =
                new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);

        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            System.out.println("indexName: " + getResponse.getIndex());
            System.out.println("indexType: " + getResponse.getType());
            System.out.println("indexId: " + getResponse.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*??????????????????*/
    public List search() {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        StringBuilder stringBuilder = new StringBuilder();
        List list = new ArrayList<>();
        try {
            SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
            search.getHits().forEach((hits) -> {
//                stringBuilder.append("")
                list.add(hits);
            });
            log.info(list.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*????????????index*/
    public List searchIndex() {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        List list = new ArrayList<>();
        try {
            SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();
            for (SearchHit hit : hits.getHits()) {
                String index = hit.getIndex();
                list.add(index);
            }
            log.info(list.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(list);
        return list;
    }

    /*????????????*/
    public void delete() {
        DeleteRequest request = new DeleteRequest(ES_INDEX,
                ES_INDEX_TYPE,
                ES_INDEX_ID);

        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            String index = deleteResponse.getIndex();
            log.info("???????????????:" + index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*create jsonString index*/
    public void createJsonStringIndex() {
        // TODO: 2020/4/14 deprecated
//        IndexRequest request = new IndexRequest(
//                ES_INDEX,
//                "doc",
//                "1");
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2020-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        /*?????????????????????*/
//        request.routing("routing");
//        request.parent("parent");
//        request.timeout(TimeValue.timeValueSeconds(1));
//        request.timeout("1s");
//        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
//        request.setRefreshPolicy("wait_for");
//        request.version(2);
//        request.versionType(VersionType.EXTERNAL);
//        request.opType(DocWriteRequest.OpType.CREATE);
//        request.opType("create");
//        request.setPipeline("pipeline");

        /*???????????????????????? listener */
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println(ES_INDEX + " :????????????????????????");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("????????????????????????");
            }
        };
        client.indexAsync(request, RequestOptions.DEFAULT, listener);
    }


    /*create map index*/
    public void createMapIndex() {

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy5");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        // TODO: 2020/4/14 deprecated
//        IndexRequest indexRequest = new IndexRequest("posts", "doc", "5")
//                .source(jsonMap);
        IndexRequest indexRequest = new IndexRequest("posts").id("1").source(jsonMap);

        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println(indexResponse.getIndex() + "??????????????????");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("??????????????????");
            }
        };
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);

    }

    /*create XContentFactory index*/
    public void createXContentFactoryIndex() {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("user", "kimchy");
                builder.timeField("postDate", new Date());
                builder.field("message", "trying out Elasticsearch");
            }
            builder.endObject();
            // TODO: 2020/4/14 deprecated
//            IndexRequest indexRequestXContentFactory = new IndexRequest("posts", "doc", "1")
//                    .source(builder);
            IndexRequest indexRequestXContentFactory = new IndexRequest("posts").id("1").source(builder);

            client.index(indexRequestXContentFactory, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*create source -> key-pairs index*/
    public void createKeyPairsIndex() {
        IndexRequest indexRequestKeyPairs = new IndexRequest("posts").id("1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch"
                );
        try {
            client.index(indexRequestKeyPairs, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*????????????????????????*/
//        IndexRequest request1 = new IndexRequest("posts", "doc", "1")
//                .source("field", "value")
//                .version(1);
//        try {
//            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//        } catch (ElasticsearchException e) {
//            if (e.status() == RestStatus.CONFLICT) {
//
//            }
//        }
    }

    @Test
    /**
     * ????????????
     * @throws Exception
     */
    public void bulk() {

        GetRequest getRequest = new GetRequest(
                "posts",
                "doc",
                "5");

        boolean exists = false;
        try {
            exists = client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exists) {
            log.info("posts" + "????????????");
        } else {
            log.info("posts" + "???????????????,????????????........");
            BulkRequest request = new BulkRequest();
            request.add(new IndexRequest("posts", "doc", "5")
                    .source(XContentType.JSON, "emp", "??????", "salary", 1000, "job", null));

            BulkResponse bulkResponses = null;
            try {
                bulkResponses = client.bulk(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        BulkRequest request = new BulkRequest();

        //????????????source(XContentType.JSON,key:value,key:value)
//        request.add(new DeleteRequest("posts", "doc", "5"));
//        request.add(new UpdateRequest("posts", "doc", "1")
//                .doc(XContentType.JSON, "other", "test","age",11,"name","??????"));
//        request.add(new IndexRequest("posts", "doc", "5")
//                .source(XContentType.JSON, "emp", "??????","salary",1000,"job",null));

        //????????????
//        BulkResponse bulkResponses = null;
//        try {
//            bulkResponses = client.bulk(request, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //????????????,??????????????????listener
        /*ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.bulkAsync(request, RequestOptions.DEFAULT, listener);*/

        //Bulk Response ???????????????????????????
        /*for (BulkItemResponse bulkItemResponse: bulkResponses){
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();

            if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                    || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                IndexResponse indexResponse = (IndexResponse) itemResponse;

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                UpdateResponse updateResponse = (UpdateResponse) itemResponse;

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
            }
        }

        for (BulkItemResponse bulkItemResponse : bulkResponses) {
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

            }
        }*/

        /**
         * Bulk Processor ????????????????????? customListener
         * */
        /*BulkProcessor.Listener customListener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {

            }
            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                for (BulkItemResponse item : bulkResponse.getItems()) {
                    log.info(item.getIndex() + "??????????????????");
                }
            }
            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {

            }
        };

        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                (request1, bulkListener) -> client.bulkAsync(request1, RequestOptions.DEFAULT, bulkListener);
        BulkProcessor bulkProcessor = BulkProcessor.builder(bulkConsumer, customListener).build();

        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer1 =
                (request2, bulkListener) -> client.bulkAsync(request2, RequestOptions.DEFAULT, bulkListener);
        BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer1, customListener);
        builder.setBulkActions(500);
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(0);
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
        builder.setBackoffPolicy(BackoffPolicy
                .constantBackoff(TimeValue.timeValueSeconds(1L), 3));

        //Once the BulkProcessor is created requests can be added to it:
        IndexRequest one = new IndexRequest("posts", "doc", "11").
                source(XContentType.JSON, "title",
                        "In which order are my Elasticsearch queries executed?");
        IndexRequest two = new IndexRequest("posts", "doc", "12")
                .source(XContentType.JSON, "title",
                        "Current status and upcoming changes in Elasticsearch");
        IndexRequest three = new IndexRequest("posts", "doc", "13")
                .source(XContentType.JSON, "title",
                        "The Future of Federated Search in Elasticsearch");
        bulkProcessor.add(one);
        bulkProcessor.add(two);
        bulkProcessor.add(three);

        try {
            boolean terminated = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }


    //    @Test
    public JSONObject bulk2() {
        String str = "{\"customIndex\":[{\"indexName\":\"index_text101\",\"indexType\":\"doc\",\"fieldName\":\"emp\",\"fieldValue\":\"dev\",\"id\":\"1001\"},{\"indexName\":\"index_text102\",\"indexType\":\"doc\",\"fieldName\":\"emp\",\"fieldValue\":\"dev\",\"id\":\"1002\"}],\"host\":\"bigdata1\",\"port\":9200}";

        JSONObject jsonCustomIndex;
        jsonCustomIndex = JSONObject.parseObject(str);
        String host = jsonCustomIndex.getString("host");
        int port = Integer.parseInt(jsonCustomIndex.getString("port"));
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, "http")));
        JSONArray arrayIndex = jsonCustomIndex.getJSONArray("customIndex");
        BulkRequest request = new BulkRequest();
        GetRequest getRequest;
        for (int i = 0; i < arrayIndex.size(); i++) {
            JSONObject jsonIndex = arrayIndex.getJSONObject(i);
            String indexName = jsonIndex.getString("indexName");
            String indexType = jsonIndex.getString("indexType");
            String fieldName = jsonIndex.getString("fieldName");
            String fieldValue = jsonIndex.getString("fieldValue");
            String id = jsonIndex.getString("id");
            getRequest = new GetRequest(
                    indexName,
                    indexType,
                    id);

            boolean exists = false;
            try {
                exists = client.exists(getRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (exists) {
                log.info(indexName + "???????????????,????????????........");
                request.add(new UpdateRequest(indexName, indexType, id)
                        .doc(XContentType.JSON, fieldName, fieldValue));
            } else {
                log.info(indexName + "???????????????,????????????........");

                request.add(new IndexRequest(indexName, indexType, id)
                        .source(XContentType.JSON, fieldName, fieldValue));
            }

        }
//        BulkResponse bulkResponses = null;
//        StringBuilder builder = new StringBuilder();
        try {
            client.bulk(request, RequestOptions.DEFAULT);
//            for (BulkItemResponse item : bulkResponses.getItems()) {
//                String index = item.getIndex();
//                builder.append("{");

//                builder.append(index);
//            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        log.info("jsonCustomIndex" + jsonCustomIndex.toJSONString());
        return jsonCustomIndex;
    }
}
