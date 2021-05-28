package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Create by wong on 2021/5/28
 */
//SpringCloud对Feign进行了增强兼容了SpringMVC的注解 ，我们在使用SpringMVC的注解时需要注意：
//1、feignClient接口 有参数在参数必须加@PathVariable("XXX")和@RequestParam("XXX")
//2、feignClient返回值为复杂对象时其类型必须有无参构造函数。
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)// 指定远程调用的服务名
public interface CmsPageClient {
    // 根据页面id，远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")// 用GetMapping标识远程调用的http的方法类型
    public CmsPage findCmsPageById(@PathVariable("id") String id);//方法名可随便定义，下同

    // 添加页面
    @PostMapping("/cms/page/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);
}
