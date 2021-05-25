package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create by wong on 2021/5/25
 */
@Service
public class SysdictionaryService {

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    // 根据字典分类type查询字典信息
    public SysDictionary findBydType(String dType) {
        return sysDictionaryRepository.findBydType(dType);
    }

}
