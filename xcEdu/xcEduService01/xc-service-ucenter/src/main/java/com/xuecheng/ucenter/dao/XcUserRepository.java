package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Create by wong on 2021/6/7
 */
public interface XcUserRepository extends JpaRepository<XcUser, String> {
    // 根据用户账号查询XcUser信息
    XcUser findByUsername(String username);
}
