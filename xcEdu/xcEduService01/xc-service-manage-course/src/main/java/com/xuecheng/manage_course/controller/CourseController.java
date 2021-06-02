package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
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

    // 查询我的课程列表
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseService.findCourseList(page, size, courseListRequest);
    }

    // 新增课程
    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    // 查询课程基本信息
    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseBaseById(courseId);
    }

    // 更新课程基本信息
    @Override
    @PutMapping("/coursebase/update/{courseId}")
    public ResponseResult updateCourseBase(@PathVariable("courseId") String courseId, @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(courseId, courseBase);
    }

    // 查询课程营销信息
    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    // 更新课程营销信息
    @Override
    @PutMapping("/coursemarket/update/{courseId}")
    public ResponseResult updateCourseMarket(@PathVariable("courseId") String courseId, @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(courseId, courseMarket);
    }

    // 添加课程图片
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId, pic);
    }

    // 查询课程图片
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    // 删除课程图片
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    // 课程视图查询
    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCoruseView(id);
    }

    // 预览课程
    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    // 发布课程
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    // 保存课程计划和媒资文件关联
    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }



}
