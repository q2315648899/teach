package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Create by wong on 2021/5/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RestTemplateTest {

    // SpringMVC提供 RestTemplate请求http接口，RestTemplate的底层可以使用第三方的http客户端工具实现http 的请求
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testRestTemplate() {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getModel/5a791725dd573c3574ee333f", Map.class);
        Map map = forEntity.getBody();
        System.out.println(map);
    }

}
