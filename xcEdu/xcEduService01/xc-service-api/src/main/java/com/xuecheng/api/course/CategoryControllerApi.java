package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Create by wong on 2021/5/25
 */
@Api(value = "课程分类管理接口", description = "课程分类管理")
public interface CategoryControllerApi {

    @ApiOperation("查询课程分类")
    public CategoryNode findList();
}
