package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Create by wong on 2021/6/10
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse, String> {
    //根据用户id和课程id查询选课记录，用于判断是否添加选课
    XcLearningCourse findByUserIdAndCourseId(String userId, String courseId);
}
