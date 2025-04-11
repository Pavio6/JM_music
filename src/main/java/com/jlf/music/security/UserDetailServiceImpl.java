package com.jlf.music.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        log.info("根据username查询用户");
        SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, username));
        if (Objects.isNull(sysUser)) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
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
        log.info("验证成功, 返回loginUser");
        // 3. 返回自定义的 LoginUser（包含用户信息和权限）
        return new LoginUser(sysUser, authorities);
    }
}
