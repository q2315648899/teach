package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Create by wong on 2021/6/7
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {
    // 根据用户id查询所属的公司id
    XcCompanyUser findByUserId(String userId);
}

