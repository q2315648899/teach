package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Create by wong on 2021/6/5
 */
@Service
public class AuthService {

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 用户认证申请令牌
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 密码模式远程请求spring security获取JWT令牌
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //将token令牌存储到redis
        String access_token = authToken.getAccess_token();
        String content = JSON.toJSONString(authToken);
        boolean saveTokenResult = saveToken(access_token, content, tokenValiditySeconds);
        if (!saveTokenResult) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    // 密码模式远程请求spring security获取JWT令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        // 采用客户端负载均衡，从eureka获取认证服务的ip 和端口（因为spring security在认证服务中）
        // 从eureka中获取认证服务的一个实例的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        // 此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        // 设置令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";
        //请求的内容分两部分
        //1、header信息，包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        // 返回的httpbasic自带"Basic WGNXZWJBcHA6WGNXZWJBcHA="前缀
        String httpbasic = getHttpBasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);//"Basic WGNXZWJBcHA6WGNXZWJBcHA="
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);

        //exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
        /**
         * 参数介绍：
         * url就是 申请令牌的url /oauth/token
         * method http的方法类型
         * requestEntity请求内容
         * responseType，将响应的结果生成的类型
         */
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);

        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        //远程调用申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        Map bodyMap = exchange.getBody();
        if (bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null) {//jti是jwt令牌的唯一标识作为用户身份令牌
            //解析spring security返回的错误信息
            if (bodyMap != null && bodyMap.get("error_description") != null) {
                String error_description = (String) bodyMap.get("error_description");
                if (StringUtils.isNotEmpty(error_description)) {
                    if (error_description.equals("坏的凭证")) {
                        ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                    } else if (error_description.indexOf("UserDetailsService returned null") >= 0) {
                        ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                    }
                }

            }
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodyMap.get("jti"));////设置身份令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));////设置刷新令牌
        authToken.setJwt_token((String) bodyMap.get("access_token"));////设置JWT令牌
        return authToken;
    }

    /**
     * 根据客户端id和客户端密码获取Basic的串
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId + ":" + clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    /**
     * 保存令牌信息到redis数据库
     *
     * @param access_token 用户身份令牌标识作为key
     * @param content      令牌全部内容，就是AuthToken对象的内容
     * @param ttl          有效时间
     * @return
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        //令牌名称
        String key = "user_token:" + access_token;
        //保存到令牌到redis
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        //获取过期时间，判断是否保存成功
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    // 拿身份令牌从redis中查询jwt令牌
    public AuthToken getUserToken(String access_token) {
        String key = "user_token:" + access_token;
        // 从redis中取得令牌信息
        String userTokenString = stringRedisTemplate.opsForValue().get(key);
        AuthToken authToken = null;
        try {
            // 转成对象
            authToken = JSON.parseObject(userTokenString, AuthToken.class);
            return authToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 从redis中删除jwt令牌
    public boolean delToken(String access_token) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.delete(key);
        return true;
    }

}
