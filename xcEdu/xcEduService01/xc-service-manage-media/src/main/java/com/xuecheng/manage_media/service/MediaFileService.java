package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by wong on 2021/6/2
 */
@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    // 查询我的文件列表
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //条件值对象
        MediaFile mediaFile = new MediaFile();
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        // 自定义条件查询
        // 定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())//tag字段模糊匹配
                .withMatcher("fileOriginalName",
                        ExampleMatcher.GenericPropertyMatchers.contains())//文件原始名称模糊匹配
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());//处理状态精确匹配（默认，可不设置）

        //查询条件对象
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }

        // 定义Example条件对象
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);
        // 分页参数
        if (page <= 0) {
            page = 1;
        }

        page = page - 1;
        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size);
        // 分页查询
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);// 实现自定义条件查询并且分页查询
        // 总记录数（除掉分页参数时，根据查询条件查询到的总记录数）
        long totalElements = all.getTotalElements();
        // 数据列表
        List<MediaFile> content = all.getContent();
        // 返回的数据集
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setList(content);// 数据列表
        queryResult.setTotal(totalElements);// 数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }
}
