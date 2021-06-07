package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Create by wong on 2021/6/5
 */
@RestController
public class AuthController implements AuthControllerApi {
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;
    @Autowired
    AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        // 账号
        String username = loginRequest.getUsername();
        // 密码
        String password = loginRequest.getPassword();
        // 校验账号是否输入
        if (loginRequest == null || StringUtils.isEmpty(username)) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        // 校验密码是否输入
        if (StringUtils.isEmpty(password)) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }

        AuthToken authToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), clientId, clientSecret);
        // 将用户身份令牌写入cookie
        // 访问token
        String access_token = authToken.getAccess_token();
        // 将访问令牌存储到cookie
        this.saveCookie(access_token);
        return new LoginResult(CommonCode.SUCCESS, access_token);

    }

    // 将令牌保存到cookie
    private void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getResponse();
        // addCookie(HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly)
        // 添加cookie 认证令牌，最后一个参数设置为false，表示允许浏览器获取
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }

    // 从cookie删除令牌token
    private void clearCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        // 取出cookie中的身份令牌
        String access_token = getTokenFormCookie();
        // 删除redis中的令牌token
        boolean result = authService.delToken(access_token);
        // 删除cookie中的令牌token
        this.clearCookie(access_token);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        // 取出cookie中的用户身份令牌
        String access_token = getTokenFormCookie();
        if (access_token == null) {
            return new JwtResult(CommonCode.FAIL, null);
        }
        // 拿身份令牌从redis中查询jwt令牌
        AuthToken authToken = authService.getUserToken(access_token);
        if (authToken == null) {
            return new JwtResult(CommonCode.FAIL, null);
        }

        // 将jwt令牌返回给用户
        return new JwtResult(CommonCode.SUCCESS, authToken.getJwt_token());
    }

    //从cookie中读取身份令牌
    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        if (cookieMap != null && cookieMap.get("uid") != null) {
            String access_token = cookieMap.get("uid");
            return access_token;
        }
        return null;
    }

}
