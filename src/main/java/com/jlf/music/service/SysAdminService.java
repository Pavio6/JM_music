package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.AdminLoginDTO;
import com.jlf.music.entity.SysAdmin;
import com.jlf.music.controller.vo.AdminLoginVo;

public interface SysAdminService extends IService<SysAdmin> {
    /**
     * 管理员登录
     * @param adminLoginDTO 登录表单信息
     * @return token等信息
     */
    AdminLoginVo login(AdminLoginDTO adminLoginDTO);


}
