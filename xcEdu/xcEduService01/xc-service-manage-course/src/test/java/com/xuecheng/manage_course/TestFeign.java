package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {
    @Autowired
    CmsPageClient cmsPageClient;// 接口代理对象，有Feign生成代理对象

    @Test
    public void testRibbon() {
        // 发起远程调用
        CmsPage cmsPage = cmsPageClient.findCmsPageById("60a7a23d5089c30914279846");
        System.out.println(cmsPage);
    }
}
