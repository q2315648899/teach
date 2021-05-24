package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Create by wong on 2021/5/24
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    // 课程计划查询
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanListById(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanListById(courseId);
    }

    // 添加课程计划
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult AddTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }
}