package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Administrator.
 */
public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {

    // 根据字典类型查询字典信息
    public SysDictionary findBydType(String dType);
}
