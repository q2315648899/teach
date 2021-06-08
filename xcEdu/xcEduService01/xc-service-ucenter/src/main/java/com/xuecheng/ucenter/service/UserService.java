package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by wong on 2021/6/7
 */
@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    // 根据用户账号查询XcUser信息
    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findByUsername(username);
    }

    // 根据账号查询用户的信息，并返回用户扩展信息
    public XcUserExt getUserExt(String username) {
        // 根据用户账号查询XcUser信息
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        // 用户id
        String userId = xcUserExt.getId();
        // 查询用户所有权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        // 根据用户id查询所属的公司id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        if (xcCompanyUser != null) {
            // 取到用户所属的公司id
            String companyId = xcCompanyUser.getCompanyId();
            xcUserExt.setCompanyId(companyId);
        }
        // 设置权限
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }


}
