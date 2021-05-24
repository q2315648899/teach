package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.manage_course.dao.TeachPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create by wong on 2021/5/24
 */
@Service
public class CourseService {

    @Autowired
    TeachPlanMapper teachPlanMapper;

    // 课程计划查询
    public TeachplanNode findTeachplanListById(String courseId) {
        return teachPlanMapper.selectList(courseId);
    }
}
