package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by wong on 2021/5/29
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;

    // 搜索type下的全部记录
    @Test
    public void testSearchAll() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    // 分页查询
    @Test
    public void testSearchPage() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置分页参数
        // 页码
        int page = 1;
        // 每页记录数
        int size = 2;
        // 计算出起始下标
        int from = (page - 1) * size;
        searchSourceBuilder.from(from); // 分页查询，设置起始下标，从0开始
        searchSourceBuilder.size(size);// 每页显示个数
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数(不算分页参数的查询结果)
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    // Term Query
    // Term Query为精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
    @Test
    public void testTermQuery() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // 设置Term Query查询
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring开发"));
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    // 根据id精确匹配
    @Test
    public void testTermQueryByIds() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // 根据id查询
        // 定义查询的id
        String[] split = new String[]{"1","2"};
        List<String> idList = Arrays.asList(split);
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", idList));
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    // MatchQuery
    @Test
    public void testMatchQuery() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // MatchQuery
        /*“spring开发框架”会被分为三个词：spring、开发、框架
        设置"minimum_should_match": "80%"表示，三个词在文档的匹配占比为80%，即3*0.8=2.4，向上取整得2，表
        示至少有两个词在文档中要匹配成功。*/
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发框架").minimumShouldMatch("80%"));
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    // MultiMatchQuery
    @Test
    public void testmultiMatchQuery() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        // 指定类型
        searchRequest.types("doc");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // MultiMatchQuery
//        提升boost，通常关键字匹配上name的权重要比匹配上description的权重高，这里可以对name的权重提升。
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring框架","name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10));//提升boost权重,10倍
        // 设置source源字段过滤。第一个参数结果集包括哪些字段，第二个参数结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历匹配度高的文档
        for (SearchHit hit : searchHits) {
            // 文档所在的索引
            String index = hit.getIndex();
            // 文档所在的type类型
            String type = hit.getType();
            // 文档的主键
            String id = hit.getId();
            // 文档的匹配度得分
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于上边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

}
