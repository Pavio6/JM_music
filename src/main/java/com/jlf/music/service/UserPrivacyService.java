package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.UserPrivacyDTO;
import com.jlf.music.entity.UserPrivacy;

public interface UserPrivacyService extends IService<UserPrivacy> {
    /**
     * 修改用户隐私设置
     * @param privacyDTO 隐私dto
     * @return Boolean
     */
    Boolean updatePrivacySettings(UserPrivacyDTO privacyDTO);

    /**
     * 获取用户隐私设置
     * @param id 用户ID
     * @return dto
     */
    UserPrivacyDTO getPrivacySettings(Long id);
}
