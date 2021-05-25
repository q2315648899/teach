package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Create by wong on 2021/5/24
 */
@Mapper
public interface TeachPlanMapper {
    // 课程计划查询
    TeachplanNode selectList(String courseId);
}
