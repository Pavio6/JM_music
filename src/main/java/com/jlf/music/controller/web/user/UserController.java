package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.common.constant.RedisConstant;
import com.jlf.music.controller.dto.*;
import com.jlf.music.controller.qry.FollowListQry;
import com.jlf.music.controller.qry.UserQry;
import com.jlf.music.controller.vo.*;
import com.jlf.music.entity.SysUser;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static com.jlf.music.common.constant.Constant.SUCCESS_CODE;

/**
 * 用户控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserRegisterVo> register(@Valid UserRegisterDTO registerDTO,
                                           @RequestPart(value = "userAvatar", required = false) MultipartFile userAvatarFile,
                                           BindingResult bindingResult) {
        return Result.success(sysUserService.register(registerDTO, userAvatarFile, bindingResult));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserLoginDTO userLoginDTO) {
        return Result.success(sysUserService.login(userLoginDTO));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!Boolean.TRUE.equals(stringRedisTemplate.delete(RedisConstant.USER_LOGIN_KEY + token))) {
            log.warn("退出登录失败!!!");
            throw new ServiceException("退出失败");
        }
        // 清除Security上下文中的认证信息
        SecurityContextHolder.clearContext();
        log.info("用户退出登录成功");
        return Result.success(SUCCESS_CODE, "退出成功");
    }

    /**
     * 更新用户资料
     */
    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> updateUser(@RequestParam(value = "userName", required = false) String userName,
                                      @RequestParam(value = "userEmail", required = false) String userEmail,
                                      @RequestParam(value = "userBio", required = false) String userBio,
                                      @RequestParam(value = "userBirth", required = false) LocalDate userBirth,
                                      @RequestParam(value = "userSex", required = false) Integer userSex,
                                      @RequestParam(value = "userAvatar", required = false) MultipartFile userAvatar) {

        return Result.success(sysUserService.updateUser(userName, userEmail, userBio, userBirth, userSex, userAvatar));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}/info")
    public Result<UserDetailInfoVo> getUserById(@PathVariable(value = "userId") Long userId) {
        return Result.success(sysUserService.getUserInfo(userId));
    }

    /**
     * 获取用户个人信息
     *
     * @return UserInfoVo
     */
    @GetMapping("/mine")
    public Result<UserPersonalInfoVo> getMine() {
        return Result.success(sysUserService.getMine());
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Boolean> changePassword(@Valid @RequestBody PasswordDTO passwordDTO, BindingResult bindingResult) {
        return Result.success(sysUserService.changePassword(passwordDTO, bindingResult));
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<IPage<SysUser>> getUsers(UserQry userQry) {
        return Result.success(sysUserService.getUserList(userQry));
    }

    /**
     * 获取用户个人的关注列表
     */
    @GetMapping("/mine/follow/list")
    public Result<IPage<SimpleItemVo>> getMineFollowList(FollowListQry followListQry) {
        return Result.success(sysUserService.getFollowList(followListQry));
    }

    /**
     * 获取用户个人的粉丝列表
     */
    @GetMapping("/mine/fan/list")
    public Result<IPage<SimpleItemVo>> getMineFanList(PageRequest pageRequest) {
        return Result.success(sysUserService.getFanList(pageRequest));
    }
}
