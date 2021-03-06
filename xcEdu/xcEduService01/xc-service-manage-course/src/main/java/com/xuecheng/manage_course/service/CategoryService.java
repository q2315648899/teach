package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create by wong on 2021/5/25
 */
@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    // 查询课程分类
    public CategoryNode findList() {
        CategoryNode categoryNode = categoryMapper.findList();
        return categoryNode;
    }
}
