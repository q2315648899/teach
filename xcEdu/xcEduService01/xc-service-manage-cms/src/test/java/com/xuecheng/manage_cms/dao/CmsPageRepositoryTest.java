package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Create by wong on 2021/5/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll() {
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    // 分页测试
    @Test
    public void testFindPage() {
        // 分页参数
        int page = 2;//从0开始
        int size = 10;//每页记录数
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    // 自定义条件查询
    @Test
    public void testFindAllByExample() {
        // 分页参数
        int page = 0;//从0开始
        int size = 10;//每页记录数
        Pageable pageable = PageRequest.of(page, size);

        // 条件值对象
        CmsPage cmsPage = new CmsPage();
        // 要查询5a751fab6abb5044e0d19ea1站点的页面
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        // 设置模板id条件
//        cmsPage.setTemplateId("5a925be7b00ffc4b3c1578b5");
        // 设置页面别名
        cmsPage.setPageAliase("页面");
        // 条件匹配器
//        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
//        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
//        页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
//        ExampleMatcher.GenericPropertyMatchers.contains()包含关键字
//        ExampleMatcher.GenericPropertyMatchers.exact()精准匹配
        // 定义Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }

    // 添加
    @Test
    public void testInsert() {
    // 定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    // 删除
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("60a23dc65089c33d64683786");
    }

    // 修改
    @Test
    public void testUpdate() {
        Optional<CmsPage> optional = cmsPageRepository.findById("5a754adf6abb500ad05688d9");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageAliase("测试页面01");
            cmsPageRepository.save(cmsPage);
        }
    }

    // 根据页面名称查询
    @Test
    public void testFindByPageName() {
        CmsPage cmsPage = cmsPageRepository.findByPageName("index_banner.html");
        System.out.println(cmsPage);
    }

}
