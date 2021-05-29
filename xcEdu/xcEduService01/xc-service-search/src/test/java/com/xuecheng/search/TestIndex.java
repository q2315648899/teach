package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by wong on 2021/5/29
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;

    // 创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        // 创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1)
                .put("number_of_replicas", 0));
        //设置映射
        createIndexRequest.mapping("doc", " {\n" +
                " \t\"properties\": {\n" +
                " \"name\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                " },\n" +
                " \"description\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                " },\n" +
                " \"studymodel\": {\n" +
                " \"type\": \"keyword\"\n" +
                " },\n" +
                " \"price\": {\n" +
                " \"type\": \"float\"\n" +
                " }\n" +
                " }\n" +
                "}", XContentType.JSON);
        // 操作索引的客户端
        IndicesClient indices = client.indices();
        // 创建索引
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        // 创建索引响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    // 删除索引库
    @Test
    public void testDeleteIndex() throws IOException {
        // 删除索引请求对象，并设置索引名称
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        // 操作索引的客户端
        IndicesClient indices = client.indices();
        // 删除索引
        DeleteIndexResponse deleteIndexResponse = indices.delete(deleteIndexRequest);
        // 删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    // 添加文档
    @Test
    public void testAddDoc() throws IOException {
        // 准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud基础入门 " +
                "3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        // 索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");
        // 指定索引文档内容
        indexRequest.source(jsonMap);
        // 索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }

    // 查询文档
    @Test
    public void testGetDoc() throws IOException {
        // 查询请求对象
        GetRequest getRequest = new GetRequest(
                "xc_course",
                "doc",
                "3pKit3kBFQvXgNqeeInz");
        GetResponse getResponse = client.get(getRequest);
        boolean exists = getResponse.isExists();
        // 得到文档的内容
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    // 更新文档
    // 使用 Client更新文档的方法是局部更新方法。可以指定文档的部分字段也可以指定完整的文档内容。
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc",
                "3pKit3kBFQvXgNqeeInz");
        Map<String, String> map = new HashMap<>();
        map.put("name", "spring cloud实战");
        updateRequest.doc(map);
        UpdateResponse update = client.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    // 根据id删除文档
    @Test
    public void testDelDoc() throws IOException {
        // 删除文档id
        String id = "3pKit3kBFQvXgNqeeInz";
        // 删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("xc_course", "doc", id);
        // 响应对象
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        // 获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(result);
    }
}
