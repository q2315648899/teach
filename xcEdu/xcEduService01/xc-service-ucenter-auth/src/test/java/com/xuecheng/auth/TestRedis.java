package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Create by wong on 2021/6/4
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试redis的连接和添加
     */
    @Test
    public void testRedis() {
        //定义key
        String key = "user_token:1216d14a-427f-40fb-b572-3f5678294f08";
        //定义Map
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("jwt", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYyMjg0Nzg1MywianRpIjoiMTIxNmQxNGEtNDI3Zi00MGZiLWI1NzItM2Y1Njc4Mjk0ZjA4IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.CXHC9ykUQUuMoajAXxqhzsleTllqY5XxVFGOZ6N-vhHBvYkqFNkb0Ya4i-1gPr5Itbb1910vFI5Ks3IPl3MqesqdiRwZDmFGnUkg-kYfHAPHmgBf5ZO8lH3bQ-u7wfWEyygklROnUR5l6jId__03z4orNG136O2WQhEF_ixTss5kVxvHvkyU_wfqnVUl7fDybaPYuh05C2LZvehqfT_JLnvjaJZWBUgA9g_LgNtyfqWxEOSz04B344ErVQVND_VAQ8h1WqSYQs3Fz01rRz96TCKPksDKFWBXxwHXDKgMktlE0BFI6x9uH3p7QEWlNrayHz7tRELRM3OG1Z0Q4SbQBQ");
        mapValue.put("refresh_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiIxMjE2ZDE0YS00MjdmLTQwZmItYjU3Mi0zZjU2NzgyOTRmMDgiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYyMjg0NjM0NywianRpIjoiNTEyODlhMTEtMjEzYS00MzE0LWI3NGItZmIwZDIzY2ViZDdjIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.JjUUnPFOYa21G9PrD9Ut4LMFOMcy1hsBVkf3vmsCOM8-z-XA_VY8HSBm9nu_CBWk-mKlyWLIOm_jt2IT2d_wE33l8GGYrXK2NTz_BUs57WuW-meNY02AL74fZO8PjHnCCNo0gnh4Rflc1d6_XdYkImA3Kc8A5o3skCwnoIFIqLIJsvRdyZFIzCD410zcBxtdB4L0f4Iy03O5PtQiSqCdlBvkI4uUlBUu6UQiw00sydrKQfKTOOr4l1NOsxGVqSpoITOLdsiuVZy3IbRBzvLKHJLQ7m3JFl3cMXAFOb-UJPuqS2xywpwnNyWTLK_PeI5ikoCPlmUZQhcjAaks0v431Q");
        String value = JSON.toJSONString(mapValue);
        //检查key是否存在，读取过期时间，已过期返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        //向redis中存储字符串
        stringRedisTemplate.boundValueOps(key).set(value, 60, TimeUnit.SECONDS);
        //根据key获取value
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }
}
