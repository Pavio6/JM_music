package com.jlf.music.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.dto.AdminLoginDTO;
import com.jlf.music.entity.SysAdmin;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SysAdminMapper;
import com.jlf.music.service.SysAdminService;
import com.jlf.music.utils.SaltUtils;
import com.jlf.music.controller.vo.AdminLoginVo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jlf.music.common.constant.RedisConstant.ADMIN_LOGIN_KEY;

@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin>
        implements SysAdminService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysAdminMapper sysAdminMapper;

    /**
     * 管理员登录
     *
     * @param adminLoginDTO 登录表单信息
     * @return token等信息
     */
    @Override
    public AdminLoginVo login(AdminLoginDTO adminLoginDTO) {
        // 判断验证码值是否正确
        String code = stringRedisTemplate.opsForValue().get(adminLoginDTO.getCaptchaKey());
        if (StrUtil.isEmpty(code)) {
            throw new ServiceException("验证码过期");
        }
        if (!adminLoginDTO.getCaptchaCode().toLowerCase().equals(code)) {
            throw new ServiceException("验证码错误");
        }
        // 查询管理员信息
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getAdminName, adminLoginDTO.getAdminName());
        SysAdmin sysAdmin = sysAdminMapper.selectOne(wrapper);
        if (sysAdmin == null) {
            throw new ServiceException("不存在该用户");
        }
        // 密码校验
        if (!SaltUtils.verify(adminLoginDTO.getAdminPass(), sysAdmin.getAdminSalt(), sysAdmin.getAdminPass())) {
            throw new ServiceException("用户密码错误!");
        }

        // 生成 Token
        String token = UUID.randomUUID().toString();
        Map<String, String> adminMap = new HashMap<>();
        adminMap.put("adminId", sysAdmin.getAdminId().toString());
        adminMap.put("adminName", sysAdmin.getAdminName());
        // 存储管理员基本信息到 Redis
        stringRedisTemplate.opsForHash().putAll(ADMIN_LOGIN_KEY + token, adminMap);
        // 设置有效期
        stringRedisTemplate.expire(ADMIN_LOGIN_KEY + token, 300000L, TimeUnit.MINUTES);

        // 清理验证码
        stringRedisTemplate.delete(adminLoginDTO.getCaptchaKey());

        // 封装返回结果
        AdminLoginVo adminLoginVo = new AdminLoginVo();
        adminLoginVo.setAdminName(sysAdmin.getAdminName())
                .setAdminId(sysAdmin.getAdminId())
                .setToken(token);
        return adminLoginVo;

    }




}
