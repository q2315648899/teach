package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Create by wong on 2021/5/17
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {

}
