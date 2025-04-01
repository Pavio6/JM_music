package com.jlf.music.controller.web.common;

import com.jlf.music.common.Result;
import com.jlf.music.common.constant.RedisConstant;
import com.jlf.music.controller.vo.CaptchaVo;
import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/common/captcha")
@Slf4j
public class CaptchaController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取验证码
     */
    @GetMapping("get")
    public Result<CaptchaVo> getCaptcha() {
        // 创建一个验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 获取验证码信息 并转为小写
        String code = specCaptcha.text().toLowerCase();
        // 为验证码生成一个key值  用来唯一标识
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
        log.info("正在获取验证码...");
        // 将 key code值 存放到redis中
        stringRedisTemplate
                .opsForValue()
                .set(key, code, RedisConstant.LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS); // 单位为秒
        return Result.success(new CaptchaVo(specCaptcha.toBase64(), key));
    }
}
