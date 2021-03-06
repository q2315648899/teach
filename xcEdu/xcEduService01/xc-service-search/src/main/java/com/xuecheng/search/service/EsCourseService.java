package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by wong on 2021/5/30
 */
@Service
public class EsCourseService {

    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;
    @Value("${xuecheng.elasticsearch.media.index}")
    private String media_index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;
    @Value("${xuecheng.elasticsearch.media.type}")
    private String media_type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field;
    @Value("${xuecheng.elasticsearch.media.source_field}")
    private String media_source_field;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    // ????????????
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        // ????????????????????????
        SearchRequest searchRequest = new SearchRequest(es_index);
        // ????????????
        searchRequest.types(es_type);
        // ?????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ??????source???????????????
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array, new String[]{});

        // ??????BoolQuery??????????????????????????????????????????????????????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // ????????????
        // ???????????????????????????
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            // ???????????????MultiMatchQuery
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        // ????????????????????????
        // ???????????????
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            // ??????????????????
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            // ??????????????????
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            // ??????????????????
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }

        // ????????????????????????
        searchSourceBuilder.query(boolQueryBuilder);
        // ??????????????????
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 12;
        }
        // ?????????????????????
        int from = (page - 1) * size;
        searchSourceBuilder.from(from); // ???????????????????????????????????????0??????
        searchSourceBuilder.size(size);// ??????????????????

        // ????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");//????????????
        highlightBuilder.postTags("</font>");//????????????
        // ??????????????????
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        // ???????????????????????????????????????
        searchRequest.source(searchSourceBuilder);

        QueryResult queryResult = new QueryResult();
        List<CoursePub> coursePubList = new ArrayList<>();
        try {
            // ??????????????????ES??????http??????
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // ????????????
            SearchHits hits = searchResponse.getHits();
            // ????????????????????????
            long totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits);
            // ???????????????????????????
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                CoursePub coursePub = new CoursePub();
                // ?????????
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                // ??????id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                // ??????name
                String name = (String) sourceAsMap.get("name");

                //????????????????????????
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null) {
                    HighlightField nameField = highlightFields.get("name");
                    if (nameField != null) {
                        Text[] fragments = nameField.getFragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text str : fragments) {
                            stringBuffer.append(str.string());
                        }
                        name = stringBuffer.toString();
                    }
                }

                coursePub.setName(name);
                // ????????????
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                // ??????
                Double price = null;
                try {
                    if (sourceAsMap.get("price") != null) {
                        price = (Double) sourceAsMap.get("price");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                // ?????????
                Double price_old = null;
                try {
                    if (sourceAsMap.get("price_old") != null) {
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                // ???coursePub????????????list
                coursePubList.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(coursePubList);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    // ??????ES???????????????ES????????????????????????
    public Map<String, CoursePub> getAll(String id) {
        // ??????????????????????????????
        SearchRequest searchRequest = new SearchRequest(es_index);
        // ????????????
        searchRequest.types(es_type);
        // ?????????????????????
        // ??????SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ??????Term Query??????
        searchSourceBuilder.query(QueryBuilders.termQuery("id", id));// ???????????????id??????????????????????????????????????????_id???
        // ?????????????????????????????????????????????????????????
        // searchSourceBuilder.featchSource()
        searchRequest.source(searchSourceBuilder);

        Map<String, CoursePub> map = new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                // ?????????????????????
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId, coursePub);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //????????????????????????????????????
    public QueryResponseResult<TeachplanMediaPub> getMedia(String[] teachplanIds) {
        // ??????????????????????????????
        SearchRequest searchRequest = new SearchRequest(media_index);
        // ????????????
        searchRequest.types(media_type);

        // ??????SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //?????????????????????????????????id??????(???????????????id)
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id", teachplanIds));
        //source???????????????
        String[] source_fields = media_source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields, new String[]{});
        searchRequest.source(searchSourceBuilder);
        // ??????ES???????????????????????????Es
        SearchResponse searchResponse = null;
        try {
            //????????????
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        long total = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //????????????
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //??????????????????????????????
            String courseid = (String) sourceAsMap.get("courseid");
            String media_id = (String) sourceAsMap.get("media_id");
            String media_url = (String) sourceAsMap.get("media_url");
            String teachplan_id = (String) sourceAsMap.get("teachplan_id");
            String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
            teachplanMediaPub.setCourseId(courseid);
            teachplanMediaPub.setMediaUrl(media_url);
            teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
            teachplanMediaPub.setMediaId(media_id);
            teachplanMediaPub.setTeachplanId(teachplan_id);
            //?????????????????????
            teachplanMediaPubList.add(teachplanMediaPub);
        }

        //????????????????????????????????????
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;

    }
}
