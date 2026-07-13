package com.jlf.music.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jlf.music.common.enumerate.UserStatus;
import com.jlf.music.entity.SysUser;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, username));
        if (Objects.isNull(sysUser)) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        // 判断用户的状态
        if (Objects.equals(sysUser.getUserStatus(), UserStatus.DISABLED.getCode())) {
            log.info("用户 {} 账号被停用了", sysUser.getUserName());
            throw new ServiceException("您的账号已停用, 无法登录系统");
        }
        // 2. 根据 type 分配角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (sysUser.getType() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (sysUser.getType() == 0) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            throw new ServiceException("没有该角色");
        }
        // 3. 返回自定义的 LoginUser（包含用户信息和权限）
        return new LoginUser(sysUser, authorities);
    }
}
