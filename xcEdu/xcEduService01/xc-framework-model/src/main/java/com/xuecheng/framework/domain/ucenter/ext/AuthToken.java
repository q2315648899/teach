package com.xuecheng.framework.domain.ucenter.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by mrt on 2018/5/21.
 */
@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    String access_token;//访问token,就是短令牌。例如："jti": "89eccee7-adea-4e9b-8ac2-05de7ac924ce"
    String refresh_token;//刷新token
    String jwt_token;//jwt令牌
}
