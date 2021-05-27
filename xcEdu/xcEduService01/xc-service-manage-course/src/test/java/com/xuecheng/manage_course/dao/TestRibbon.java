package com.xuecheng.manage_course.dao;

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
public class TestRibbon {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CourseBaseRepository courseBaseRepository;

    // 负载均衡调用
    // 添加@LoadBalanced注解后，restTemplate会走LoadBalancerInterceptor拦截器，此拦截器中会通过
    // RibbonLoadBalancerClient查询服务地址，可以在此类打断点观察每次调用的服务地址和端口，两个cms服务会轮
    // 流被调用。
    @Test
    public void testRibbon() {
        // 确定要获取的服务名
        String serviceId = "xc-service-manage-cms";
        // ribbon客户端从eureka中获取服务列表，根据服务名获取服务列表
        for (int i = 0; i < 10; i++) {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://"+ serviceId +"/cms/page/get/60a7a23d5089c30914279846", Map.class);
            Map body = forEntity.getBody();
            System.out.println(body);
        }
    }
}
