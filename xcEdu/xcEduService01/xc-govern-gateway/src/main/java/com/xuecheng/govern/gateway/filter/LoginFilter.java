package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by wong on 2021/6/8
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    // 返回字符串代表过滤器的类型
    @Override
    public String filterType() {
        /**
         * 返回过滤器的类型：
         *pre：请求在被路由之前执行
         *routing：在路由请求时调用
         *post：在routing和errror过滤器之后调用
         *error：处理请求时发生错误调用
         *
         */
        return "pre";
    }

    // 返回整型数值，通过此数值来定义过滤器的执行顺序，数字越小优先级越高。
    @Override
    public int filterOrder() {
        return 0;
    }

    // 判断该过滤器是否需要执行。返回true表示要执行此过虑器，否则不执行。
    @Override
    public boolean shouldFilter() {
        return true;
    }

    // 过滤器的业务逻辑（过滤器内容）
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        // 得到request
        HttpServletRequest request = requestContext.getRequest();
        // 从cookie中查询用户身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(access_token)) {
            //拒绝访问
            access_denied();
        }
        //从redis中校验身份令牌是否过期
        long expire = authService.getExpire(access_token);
        if (expire <= 0) {
            //拒绝访问
            access_denied();
        }
        //从header中查询jwt令牌
        String jwt = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwt)) {
            //拒绝访问
            access_denied();
        }
        return null;
    }

    //拒绝访问
    private void access_denied() {
        //上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.setSendZuulResponse(false);//拒绝访问
        //设置响应内容
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String responseResultString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(responseResultString);
        //设置状态码
        requestContext.setResponseStatusCode(200);
        HttpServletResponse response = requestContext.getResponse();
        response.setContentType("application/json;charset=utf‐8");
    }
}
