package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Create by wong on 2021/5/28
 */
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)// 指定远程调用的服务名
public interface CmsPageClient {
    // 根据页面息，远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")// 用GetMapping标识远程调用的http的方法类型
    public CmsPage findCmsPageById(@PathVariable("id") String id);
}
