package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.UserStatusDTO;
import com.jlf.music.controller.dto.UserUpdateDTO;
import com.jlf.music.controller.qry.UserQry;
import com.jlf.music.entity.SysUser;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.jlf.music.common.constant.Constant.INTEGER_ONE;
import static com.jlf.music.common.constant.Constant.INTEGER_ZERO;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    @Resource
    private SysUserService sysUserService;

    /**
     * 查询用户列表 - 可携带条件
     */
    @GetMapping("/list")
    public Result<IPage<SysUser>> getUserList(UserQry userQry) {
        return Result.success(sysUserService.getUserList(userQry));
    }

    /**
     * 获取用户详情 - 管理端
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getUserDetail(@PathVariable("userId") Long userId) {
        return Result.success(sysUserService.getById(userId));
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/status")
    public Result<Boolean> updateUserStatus(@RequestBody UserStatusDTO userStatusDTO) {
        // 判断用户是否存在
        SysUser user = sysUserService.getById(userStatusDTO.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (!Objects.equals(userStatusDTO.getStatus(), INTEGER_ZERO) && !Objects.equals(userStatusDTO.getStatus(), INTEGER_ONE)) {
            throw new ServiceException("用户状态字段不合法");
        }
        user.setUserStatus(userStatusDTO.getStatus());
        // 执行更新操作
        return Result.success(sysUserService.updateById(user));
    }

    /**
     * 更新用户(管理员)信息
     */
    @PutMapping("/{userId}")
    public Result<Boolean> updateAdmin(@PathVariable("userId") Long userId,
                                      @RequestPart(value = "userFormDTO", required = false)UserUpdateDTO userFormDTO,
                                      @RequestPart(value = "userAvatarFile", required = false) MultipartFile userAvatarFile) {

        return Result.success(sysUserService.updateAdmin(userId, userFormDTO, userAvatarFile));
    }

}
