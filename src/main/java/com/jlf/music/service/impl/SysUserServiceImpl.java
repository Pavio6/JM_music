package com.jlf.music.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.MessagePermissionType;
import com.jlf.music.common.enumerate.VisibilityType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.*;
import com.jlf.music.controller.vo.*;
import com.jlf.music.entity.SysUser;
import com.jlf.music.entity.UserPrivacy;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.mapper.UserPrivacyMapper;
import com.jlf.music.security.LoginUser;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SysUserService;
import com.jlf.music.service.UserFavoriteService;
import com.jlf.music.service.UserFollowService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.USER_LOGIN_KEY;

@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private UserPrivacyMapper userPrivacyMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FileService fileService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserFollowService userFollowService;
    @Resource
    private UserFavoriteService userFavoriteService;
    // 注入 Spring Security 的密码编码器
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     *
     * @param registerDTO   注册表单DTO
     * @param bindingResult 错误信息
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO registerDTO, BindingResult bindingResult) {
        // 验证存在错误
        if (bindingResult.hasErrors()) {
            // 获取绑定错误中的第一个字段错误
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            throw new ServiceException(400, errorMessage);
        }
        // 判断用户名是否已存在
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, registerDTO.getUserName())) > 0) {
            throw new ServiceException("用户名已存在");
        }
        // 判断邮箱是否已存在

        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserEmail, registerDTO.getUserEmail())) > 0) {
            throw new ServiceException("邮箱已被注册");
        }
        // 上传用户头像到minio
        String avatarUrl = fileService.uploadImageFile(registerDTO.getUserAvatar(), UploadFileType.USER_AVATAR);
        SysUser sysUser = CopyUtils.classCopy(registerDTO, SysUser.class);
        // 密码加密
        // 使用 PasswordEncoder 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getUserPass());
        sysUser.setUserPass(encodedPassword)
                .setUserAvatar(avatarUrl)
                .setType(0);
        // 插入用户信息
        if (sysUserMapper.insert(sysUser) <= 0) {
            throw new ServiceException("注册失败");
        }

        // 注册成功后 插入默认隐私设置
        UserPrivacy userPrivacy = new UserPrivacy();
        userPrivacy.setUserId(sysUser.getUserId())
                .setProfileVisibility(VisibilityType.PUBLIC)
                .setFollowersVisibility(VisibilityType.PUBLIC)
                .setFollowingVisibility(VisibilityType.PUBLIC)
                .setPlaylistVisibility(VisibilityType.PUBLIC)
                .setMessagePermission(MessagePermissionType.ALL);

        if (userPrivacyMapper.insert(userPrivacy) <= 0) {
            throw new ServiceException("插入隐私设置失败");
        }
        return new User(sysUser.getUserId(), sysUser.getUserName(), sysUser.getUserAvatar());
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录表单DTO
     * @return Boolean
     */
    @Override
    public UserLoginVo login(UserLoginDTO userLoginDTO) {
        // 判断验证码值是否正确
        String code = stringRedisTemplate.opsForValue().get(userLoginDTO.getCaptchaKey());
        if (StrUtil.isEmpty(code)) {
            throw new ServiceException("验证码过期");
        }
        if (!userLoginDTO.getCaptchaCode().toLowerCase().equals(code)) {
            throw new ServiceException("验证码错误");
        }
        // 用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(), userLoginDTO.getUserPass());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 生成 Token
        String token = UUID.randomUUID().toString();
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        SysUser user = loginUser.getUser();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId().toString());
        userMap.put("userName", user.getUserName());
        userMap.put("userAvatar", user.getUserAvatar());
        userMap.put("authorities", loginUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
        // 存储用户基本信息到 Redis
        stringRedisTemplate.opsForHash().putAll(USER_LOGIN_KEY + token, userMap);
        // 设置有效期
        stringRedisTemplate.expire(USER_LOGIN_KEY + token, 300000L, TimeUnit.MINUTES);
        // 清理验证码
        stringRedisTemplate.delete(userLoginDTO.getCaptchaKey());
        // 封装返回结果

        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setToken(token);
        userLoginVo.setUser(new User(user.getUserId(), user.getUserName(), user.getUserAvatar()));
        return userLoginVo;
    }

    /**
     * 更新用户信息
     *
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUser(String userName, String userEmail, String userBio, LocalDate userBirth, Integer userSex, MultipartFile userAvatar) {
        Long userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("不存在该用户");
        }
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getUserId, userId);
        // 删除之前用户存放在minio中的头像地址
        if (userAvatar != null) {
            // 删除之前用户存放在minio中的头像地址
            if (sysUser.getUserAvatar() != null) {
                fileService.deleteFile(sysUser.getUserAvatar());
            }
            // 上传新的头像到minio
            String fileName = fileService.uploadImageFile(userAvatar, UploadFileType.USER_AVATAR);
            updateWrapper.set(SysUser::getUserAvatar, fileName);
        }
        // 动态设置要更新的字段
        if (userName != null) {
            updateWrapper.set(SysUser::getUserName, userName);
        }
        if (userBio != null) {
            updateWrapper.set(SysUser::getUserBio, userBio);
        }
        if (userBirth != null) {
            updateWrapper.set(SysUser::getUserBirth, userBirth);
        }
        if (userEmail != null) {
            updateWrapper.set(SysUser::getUserEmail, userEmail);
        }
        if (userSex != null) {
            updateWrapper.set(SysUser::getUserSex, userSex);
        }
        // 执行更新操作
        return sysUserMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 获取用户基本信息
     *
     * @param userId 用户ID
     * @return userInfoVo
     */
    @Override
    public UserDetailInfoVo getUserInfo(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("不存在该用户");
        }
        // 用户粉丝数和关注数
        FollowStatsVo statsVo = userFollowService.getFollowAndFollowerCount(userId);
        // 用户喜欢的歌曲列表
        IPage<SongBasicInfoVo> favoriteSongsList = userFavoriteService.getFavoriteSongsList(userId, new PageRequest());
        return new UserDetailInfoVo()
                .setUserId(sysUser.getUserId())
                .setUserAvatar(sysUser.getUserAvatar())
                .setUserBio(sysUser.getUserBio())
                .setUserName(sysUser.getUserName())
                .setFollowStatsVo(statsVo)
                .setFavoriteSongsList(favoriteSongsList);
    }

    /**
     * 修改密码
     *
     * @param passwordDTO 新 旧密码
     * @return Boolean
     */
    @Override
    public Boolean changePassword(PasswordDTO passwordDTO, BindingResult bindingResult) {
        // 验证新密码是否符合格式
        if (bindingResult.hasErrors()) {
            // 获取绑定错误中的第一个字段错误
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            throw new ServiceException(400, errorMessage);
        }
        Long userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("用户不存在");
        }
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), sysUser.getUserPass())) {
            throw new ServiceException("旧密码不正确");
        }
        // 使用 PasswordEncoder 加密新密码
        String encodedNewPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        sysUser.setUserPass(encodedNewPassword);
        return sysUserMapper.updateById(sysUser) > 0;
    }

    /**
     * 获取用户个人信息
     *
     * @return userInfoVo
     */
    @Override
    public UserPersonalInfoVo getMine() {
        Long userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("不存在该用户");
        }
        // 获取用户个人隐私设置
        UserPrivacy userPrivacy = userPrivacyMapper.selectById(sysUser.getUserId());
        // 封装返回结果
        return new UserPersonalInfoVo()
                .setUserId(sysUser.getUserId())
                .setUserName(sysUser.getUserName())
                .setUserBio(sysUser.getUserBio())
                .setUserAvatar(sysUser.getUserAvatar())
                .setUserBirth(sysUser.getUserBirth())
                .setUserEmail(sysUser.getUserEmail())
                .setUserSex(sysUser.getUserSex())
                .setUserPrivacy(CopyUtils.classCopy(userPrivacy, UserPrivacyBasicInfoVo.class));
    }


}
