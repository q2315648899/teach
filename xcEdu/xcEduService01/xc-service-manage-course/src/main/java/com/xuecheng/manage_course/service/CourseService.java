package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
    CourseMapper courseMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

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

    // 查询我的课程列表
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        // 分页参数
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 7;
        }

        // PageHelper实现分页查询
        // PageHelper设置分页参数：PageHelper.startPage(page, size)
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest);

        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(courseList.getResult());// 数据列表
        queryResult.setTotal(courseList.getTotal());// 数据总记录数

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    // 新增课程
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        // 课程默认为未发布状态
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }

    // 查询课程基本信息
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        return null;
    }

    // 更新课程基本信息
    @Transactional
    public ResponseResult updateCourseBase(String courseId, CourseBase courseBase) {
        CourseBase one = this.getCourseBaseById(courseId);
        if (one != null) {
            // 更新课程名称
            one.setName(courseBase.getName());
            // 更新适用人群
            one.setUsers(courseBase.getUsers());
            // 更新课程大分类
            one.setMt(courseBase.getMt());
            // 更新课程小分类
            one.setSt(courseBase.getSt());
            // 更新课程等级
            one.setGrade(courseBase.getGrade());
            // 更新学习模式
            one.setStudymodel(courseBase.getStudymodel());
            // 更新课程介绍
            one.setDescription(courseBase.getDescription());
            // 执行更新
            CourseBase save = courseBaseRepository.save(one);
            if (save != null) {
                return new ResponseResult(CommonCode.SUCCESS);
            }
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    // 查询课程营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseMarket courseMarket = optional.get();
            return courseMarket;
        }
        return null;
    }

    // 更新课程营销信息
    @Transactional
    public ResponseResult updateCourseMarket(String courseId, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(courseId);
        // 如果已经有营销信息，执行更新操作
        if (one != null) {
            // 更新课程收费规则
            one.setCharge(courseMarket.getCharge());
            // 更新课程价格
            one.setPrice(courseMarket.getPrice());
            // 更新课程有效性
            one.setValid(courseMarket.getValid());
            // 更新课程有效期-开始时间
            one.setStartTime(courseMarket.getStartTime());
            // 更新课程有效期-结束时间
            one.setEndTime(courseMarket.getEndTime());
            // 更新课程咨询QQ
            one.setQq(courseMarket.getQq());
            // 执行更新
            CourseMarket save = courseMarketRepository.save(one);
            if (save != null) {
                return new ResponseResult(CommonCode.SUCCESS);
            }
        } else {
            // 如果没有营销信息，新增营销信息
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            // 设置id
            one.setId(courseId);
            CourseMarket save = courseMarketRepository.save(one);
            if (save != null) {
                return new ResponseResult(CommonCode.SUCCESS);
            }
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    // 向课程管理数据添加课程与图片的关联信息
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic = null;
        // 查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        // 没有图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 查询课程图片
    public CoursePic findCoursePic(String courseId) {
        // 查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }

    // 删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        // 删除图片，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
