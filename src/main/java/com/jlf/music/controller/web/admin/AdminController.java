package com.jlf.music.controller.web.admin;

import com.jlf.music.common.Result;
import com.jlf.music.common.constant.RedisConstant;
import com.jlf.music.controller.dto.AdminLoginDTO;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SysAdminService;
import com.jlf.music.controller.vo.AdminLoginVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import static com.jlf.music.common.constant.Constant.SUCCESS_CODE;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private SysAdminService sysAdminService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 管理员登录
     * @param adminLoginDTO 登录表单信息
     * @return token等信息
     */
    @PostMapping("/login")
    public Result<AdminLoginVo> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        return Result.success(sysAdminService.login(adminLoginDTO));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!Boolean.TRUE.equals(stringRedisTemplate.delete(RedisConstant.ADMIN_LOGIN_KEY + token))) {
            throw new ServiceException("退出失败");
        }
        return Result.success(SUCCESS_CODE, "退出成功");
    }
}
