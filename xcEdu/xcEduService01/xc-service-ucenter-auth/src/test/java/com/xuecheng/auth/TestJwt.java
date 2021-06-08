package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt令牌操作的测试类
 *
 * Create by wong on 2021/6/4
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {

    //生成一个jwt令牌（基于密钥库文件生成的）
    @Test
    public void testCreateJwt() {
        //密钥库文件（是用Keytool证书管理工具生成的，里面保存了生成的密钥证书，证书里包含公钥和私钥。）
        String key_location = "xc.keystore";
        //密钥库密码
        String keystore_password = "xuechengkeystore";
        //密钥库文件路径
        ClassPathResource resource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keystore_password.toCharArray());

        //密钥别名
        String alias = "xckey";
        //密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
        //密钥对（私钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "mrt");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token=" + token);
    }

    //资源服务使用公钥验证jwt的合法性，并对jwt解码
    @Test
    public void testVerify() {
        //公钥（可通过Keytool证书管理工具，对密钥库文件导出公钥）
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //jwt令牌
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOiIxIiwicm9sZXMiOiJyMDEscjAyIiwibmFtZSI6Im1ydCIsImlkIjoiMTIzIn0.a3pPI4RIGiI-4gYmC5aO_5iqsVaigKb_vSmjTxx-AjtujjBTENxqt6OI4K9ppx0IO3WEebBeOKQr6po-PnQvmMl7aNHPd9KWfKD_he_0Im88nXWU2JBX0ARc85rFiIB190r7kjFqlZrj7YkhwUsccC7PqfiyKg7Y6B7Ca_l98Dx3Zv6VJwYxVU159XlB6G2NGMIRmDbJhnxLUG0zZdOKBs0BsQobU-IRRCI2bEu5zTImimfogNsGciTCS-7CMN8kO4T_rSMwEe66vPMA-IjchsubfseIptYNVu9_QHbc8RNGf3wWrBcZOmTxaCIH-zAo02u1TQXyeeafLQQVzRTt8g";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE2MjMxOTMzMzQsImF1dGhvcml0aWVzIjpbImNvdXJzZV9maW5kX3BpYyIsImNvdXJzZV9nZXRfYmFzZWluZm8iXSwianRpIjoiNTNlZWNjODQtZTU3Ni00ODQzLTg5YjQtYjY2N2QxNDQ5OGVlIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.jutmiBYahd1D58KjLEOjW15bz8mbNtVcyKYy3JnrW7Zqi5hbw0nVcBgUS8W8uRr5UXefWwbd_Nt0wJZHc9kJgL-R-iCy-XAWXF-vzju48MRFc7fRqg3g12GsCcb3xZt_23549lDmJlzJ9nVxfh8Vxo94Yxbfu1Fa0V8t-t1YmDrlJXbkDHEQsgTspP9UKFpFF0goCfBFeKJeTnMlyELmgEGmQXK-iYF87o5DEpvLkqaImNPsIp4MDgHmSGtnETbVt3pTE8lG3M4khOd7F7N5U230ZsM4AaZQMydyf6DXScVs3sxf3tAjKKu-VAJTT7mb349Gg5pLbERiMeK01hRIUg";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        //获取jwt原始内容（payload中原始内容）
        String claims = jwt.getClaims();
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
