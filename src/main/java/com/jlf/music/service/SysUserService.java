package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.PageRequest;
import com.jlf.music.controller.dto.*;
import com.jlf.music.controller.qry.FollowListQry;
import com.jlf.music.controller.qry.UserQry;
import com.jlf.music.controller.vo.*;
import com.jlf.music.entity.SysUser;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface SysUserService extends IService<SysUser> {
    /**
     * 用户注册
     *
     * @param registerDTO   注册表单DTO
     * @param userAvatarFile 用户头像
     * @param bindingResult 错误信息
     * @return Boolean
     */
    UserRegisterVo register(@Valid UserRegisterDTO registerDTO, MultipartFile userAvatarFile, BindingResult bindingResult);

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录表单DTO
     * @return Boolean
     */
    UserLoginVo login(UserLoginDTO userLoginDTO);

    /**
     * 更新用户信息
     *
     * @return Boolean
     */
    Boolean updateUser(String userName, String userEmail, String userBio, LocalDate userBirth, Integer userSex, MultipartFile userAvatar);

    /**
     * 获取用户基本信息
     *
     * @param userId 用户ID
     * @return UserDetailInfoVo
     */
    UserDetailInfoVo getUserInfo(Long userId);

    /**
     * 修改密码
     *
     * @param passwordDTO 新 旧密码
     * @return Boolean
     */
    Boolean changePassword(PasswordDTO passwordDTO, BindingResult bindingResult);

    /**
     * 获取用户个人信息
     *
     * @return userInfoVo
     */
    UserPersonalInfoVo getMine();

    /**
     * 获取用户列表
     */
    IPage<SysUser> getUserList(UserQry userQry);

    /**
     * 更新管理员信息
     */
    Boolean updateAdmin(Long userId, UserUpdateDTO userFormDTO, MultipartFile userAvatarFile);
    /**
     * 获取用户个人的关注列表
     */
    IPage<SimpleItemVo> getFollowList(FollowListQry followListQry);

    /**
     * 获取用户个人的粉丝列表
     */
    IPage<SimpleItemVo> getFanList(PageRequest pageRequest);
}
