package com.shiyixi.ojbackenduserservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Random;

@Component
@Slf4j
public class VerifyCodeTemplate {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String EMAIL_STORE_KEY = "email_verify_code_";

    /**
     * 生成指定长度的验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    public Integer generateValidateCode(int length) {
        Integer code = null;
        Random random = new Random(System.currentTimeMillis());
        if (length == 4) {
            code = random.nextInt(9999);//生成随机数，最大为9999
            if (code < 1000) {
                code = code + 1000;//保证随机数为4位数字
            }
        } else if (length == 6) {
            code = random.nextInt(999999);//生成随机数，最大为999999
            if (code < 100000) {
                code = code + 100000;//保证随机数为6位数字
            }
        } else {
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }

    public void setCode(String userAccount, String code) {
        stringRedisTemplate.opsForValue()
                .set(EMAIL_STORE_KEY + userAccount,
                        code,
                        Duration.ofMinutes(5));
    }

    public String getCode(String userAccount) {
        return stringRedisTemplate.opsForValue()
                .get(EMAIL_STORE_KEY + userAccount);
    }

    public void removeCode(String userAccount) {
        stringRedisTemplate.delete(EMAIL_STORE_KEY + userAccount);
    }


}
