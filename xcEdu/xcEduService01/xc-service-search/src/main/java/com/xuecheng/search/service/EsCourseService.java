package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by wong on 2021/5/30
 */
@Service
public class EsCourseService {

    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field;

    @Autowired
    RestHighLevelClient client;

    // 课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        // 创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(es_index);
        // 设置类型
        searchRequest.types(es_type);
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置source源字段过滤
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array, new String[]{});

        // 定义BoolQuery布尔查询（同时有多个查询类型时需要）
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 搜索方式
        // 根据‘关键字’搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            // 先定义一个MultiMatchQuery
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        // 根据‘分类’搜索
        //...

        // 设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        QueryResult queryResult = new QueryResult();
        List<CoursePub> coursePubList = new ArrayList<>();
        try {
            // 执行搜索，向ES发起http请求
            SearchResponse searchResponse = client.search(searchRequest);
            // 搜索结果
            SearchHits hits = searchResponse.getHits();
            // 匹配到的总记录数
            long totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits);
            // 得到匹配度高的文档
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                CoursePub coursePub = new CoursePub();
                // 源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                // 取出name
                String name = (String) sourceAsMap.get("name");
                coursePub.setName(name);
                // 取出图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                // 价格
                Double price = null;
                try {
                    if (sourceAsMap.get("price") != null) {
                        price = (Double) sourceAsMap.get("price");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                // 原价格
                Double price_old = null;
                try {
                    if (sourceAsMap.get("price_old") != null) {
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                // 将coursePub对象放入list
                coursePubList.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(coursePubList);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }
}
