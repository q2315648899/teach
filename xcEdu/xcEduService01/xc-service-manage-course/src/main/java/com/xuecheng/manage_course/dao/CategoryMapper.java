package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Create by wong on 2021/5/25
 */
@Mapper
public interface CategoryMapper {
    // 查询课程分类
    CategoryNode findList();
}
