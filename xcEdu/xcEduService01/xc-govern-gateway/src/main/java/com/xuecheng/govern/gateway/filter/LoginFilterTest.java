package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by wong on 2021/6/8
 */
@Component
public class LoginFilterTest extends ZuulFilter {

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
    // 测试需求：过虑所有请求，判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务。
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        // 得到request
        HttpServletRequest request = requestContext.getRequest();
        // 取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            // 拒绝访问
            requestContext.setSendZuulResponse(false);
            // 设置响应状态码
            requestContext.setResponseStatusCode(200);
            // 构建响应的信息
            ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            String jsonString = JSON.toJSONString(unauthenticated);
            requestContext.setResponseBody(jsonString);
            // 设置contentType
            requestContext.getResponse().setContentType("application/json;charset=UTF-8");
            return null;
        }
        return null;
    }
}
