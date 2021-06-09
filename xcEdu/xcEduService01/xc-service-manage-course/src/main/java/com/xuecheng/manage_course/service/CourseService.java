package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

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
    public QueryResponseResult findCourseList(String companyId, int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        //将教育机构id传入查询参数中
        courseListRequest.setCompanyId(companyId);
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

    // 课程视图查询，包括基本信息、图片、营销、课程计划
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        // 查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        // 查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        // 查询课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(picOptional.get());
        }
        // 查询课程计划信息
        TeachplanNode teachplanNode = teachPlanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    // 根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    // 课程预览
    public CoursePublishResult preview(String courseId) {
        // 根据id查询课程基本信息
        CourseBase one = this.findCourseBaseById(courseId);
        // 请求cms添加页面
        // 准备cmsPage的信息
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //页面模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名，就是课程名称
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);

        // 远程调用cms
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        // 拼装页面预览的url
        String previewUrl1 = previewUrl + pageId;
        // 返回CoursePublishResult对象（当中包含了页面预览的url）
        return new CoursePublishResult(CommonCode.SUCCESS, previewUrl1);
    }

    // 发布课程
    @Transactional
    public CoursePublishResult publish(String courseId) {
        // 根据id查询课程基本信息
        CourseBase one = this.findCourseBaseById(courseId);
        // 请求cms添加页面
        // 准备cmsPage的信息
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //页面模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名，就是课程名称
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        // 调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        // 保存课程的发布状态为“已发布”
        CourseBase courseBase = this.saveCoursePubState(courseId);

        // 保存课程索引信息...
        // 先创建一个CoursePub对象
        CoursePub coursePub = createCoursePub(courseId);
        // 将CoursePub对象保存到数据库
        CoursePub newCoursePub = saveCoursePub(courseId, coursePub);
        if (newCoursePub == null) {
            // 创建课程索引信息失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }
        // 课程缓存...
        // 页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        //保存课程计划媒资信息到TeachplanMediaPub表
        saveTeachplanMediaPub(courseId);
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    // 更新课程发布状态
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = this.findCourseBaseById(courseId);
        // 更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    // 创建coursePub对象
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);

        // 查询课程基础信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional != null) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }

        // 查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        // 查询课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        // 课程计划
        TeachplanNode teachplanNode = teachPlanMapper.selectList(id);
        // 将课程计划信息转成json，保存到course_pub中
        String teachplanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }

    // 将CoursePub对象保存到数据库
    public CoursePub saveCoursePub(String id, CoursePub coursePub) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }

        CoursePub coursePubNew = null;
        // 根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        }
        if (coursePubNew == null) {
            coursePubNew = new CoursePub();
        }

        // 将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub, coursePubNew);
        // 设置主键
        coursePubNew.setId(id);
        // 更新时间戳为最新时间，给logstash使用
        coursePubNew.setTimestamp(new Date());
        // 发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    //保存课程计划媒资信息
    private void saveTeachplanMediaPub(String courseId) {
        //从TeachplanMedia中查询课程媒资信息
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        // 删除TeachplanMediaPub中的数据
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        // 将teachplanMediaList课程计划媒资信息插入到TeachplanMediaPub表
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
            // 添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubList);
    }


    // 保存课程计划和媒资文件关联
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 检查当前课程计划是否是3级节点
        // 课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        // 查询课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = optional.get();
        // 只允许选择第三级的课程计划关联媒资文件
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }

        TeachplanMedia one = null;
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanId);
        if (!teachplanMediaOptional.isPresent()) {
            // 如果数据库中不存在，创建新的TeachplanMedia对象
            one = new TeachplanMedia();
        } else {
            // 如果数据库中存在，获取数据库中的TeachplanMedia对象
            one = teachplanMediaOptional.get();
        }
        //保存课程计划和媒资文件关联信息到数据库
        one.setTeachplanId(teachplanId);
        one.setCourseId(teachplanMedia.getCourseId());// 课程id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());// 媒资文件的原始名称
        one.setMediaId(teachplanMedia.getMediaId());//媒资文件的id
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件的访问url
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
