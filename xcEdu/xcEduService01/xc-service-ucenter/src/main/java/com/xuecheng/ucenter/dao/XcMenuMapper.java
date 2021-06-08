package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Create by wong on 2021/6/8
 */
@Mapper
public interface XcMenuMapper {
    // 根据用户id查询用户权限
    public List<XcMenu> selectPermissionByUserId(String userid);
}
