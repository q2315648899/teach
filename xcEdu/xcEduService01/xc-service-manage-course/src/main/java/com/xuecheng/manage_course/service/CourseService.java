package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachPlanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Create by wong on 2021/5/24
 */
@Service
public class CourseService {

    @Autowired
    TeachPlanMapper teachPlanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    // 课程计划查询
    public TeachplanNode findTeachplanListById(String courseId) {
        return teachPlanMapper.selectList(courseId);
    }

    // 添加课程计划
    // Mongodb没有事务，但该课程管理使用的mysql，有事务，增删改要加@Transactional
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        // 校验课程id和课程计划名称
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 取出课程id
        String courseid = teachplan.getCourseid();
        // 取出表单填写的父节点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            // 如果父节点为空则获取根节点
            parentid = getTeachplanRoot(courseid);
        }

        // 获取父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan1Paren = teachplanOptional.get();
        // 父节点级别
        String parentGrade = teachplan1Paren.getGrade();
        // 设置父节点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");// 未发布
        // 根据父节点级别设置新添加的课程计划节点级别
        if ("1".equals(parentGrade)) {
            teachplan.setGrade("2");
        } else if ("2".equals(parentGrade)) {
            teachplan.setGrade("3");
        }
        // 设置课程id
        teachplan.setCourseid(courseid);
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 获取课程计划根结点，如果没有则添加根结点
    private String getTeachplanRoot(String courseId) {
        // 校验课程id，查看该课程是否存在
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();

        // 获取课程计划根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() == 0) {
            // 如果没有根结点，则添加一个课程计划根结点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");//1级
            teachplanRoot.setStatus("0");
            teachplanRoot.setPname(courseBase.getName());
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        return teachplanList.get(0).getId();
    }
}
