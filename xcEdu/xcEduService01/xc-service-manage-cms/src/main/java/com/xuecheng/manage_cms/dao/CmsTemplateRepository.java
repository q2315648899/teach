package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Create by wong on 2021/5/17
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate, String> {

}
