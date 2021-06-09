package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {
    // 通过id查找课程信息
    CourseBase findCourseBaseById(String id);

    // 查询我的课程列表（请求参数为扩展的查询条件（教育机构id））
    Page<CourseInfo> findCourseList(CourseListRequest courseListRequest);
}
