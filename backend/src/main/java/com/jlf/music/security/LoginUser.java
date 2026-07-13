package com.jlf.music.security;

import com.jlf.music.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class LoginUser implements UserDetails {

    // 数据库用户实体
    private SysUser user;

    // 权限集合 (角色)  后续再补充权限
    private List<GrantedAuthority> authorities;

    public LoginUser(SysUser user, List<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }
    public LoginUser() {}


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getUserPass();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}