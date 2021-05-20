package com.xuecheng.test.freemarker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Create by wong on 2021/5/20
 */
@RequestMapping("/freemarker")
@Controller // 要输出html网页，不要使用@RestController,@RestController输出的是json数据
public class FreemarkerController {

    @RequestMapping("/test1")
    public String freemarker(Map<String, Object> map) {
        // map就是freemarker模板所使用的数据
        map.put("name", "黑马程序员");
        // 返回模板文件名称(返回freemarker模板的位置，基于resources/templates路径的)
        return "test1";
    }

}
