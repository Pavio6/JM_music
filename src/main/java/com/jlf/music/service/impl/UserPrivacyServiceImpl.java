package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.dto.UserPrivacyDTO;
import com.jlf.music.entity.UserPrivacy;
import com.jlf.music.mapper.UserPrivacyMapper;
import com.jlf.music.service.UserPrivacyService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserPrivacyServiceImpl extends ServiceImpl<UserPrivacyMapper, UserPrivacy> implements UserPrivacyService {

    @Resource
    private UserPrivacyMapper userPrivacyMapper;

    /**
     * 修改用户隐私设置
     *
     * @param privacyDTO 隐私dto
     * @return Boolean
     */
    @Override
    public Boolean updatePrivacySettings(UserPrivacyDTO privacyDTO) {
        Long userId = SecurityUtils.getUserId();
        LambdaUpdateWrapper<UserPrivacy> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPrivacy::getUserId, userId);

        if (privacyDTO.getProfileVisibility() != null) {
            updateWrapper.set(UserPrivacy::getProfileVisibility, privacyDTO.getProfileVisibility());
        }
        if (privacyDTO.getFollowersVisibility() != null) {
            updateWrapper.set(UserPrivacy::getFollowersVisibility, privacyDTO.getFollowersVisibility());
        }
        if (privacyDTO.getFollowingVisibility() != null) {
            updateWrapper.set(UserPrivacy::getFollowingVisibility, privacyDTO.getFollowingVisibility());
        }
        if (privacyDTO.getPlaylistVisibility() != null) {
            updateWrapper.set(UserPrivacy::getPlaylistVisibility, privacyDTO.getPlaylistVisibility());
        }
        if (privacyDTO.getMessagePermission() != null) {
            updateWrapper.set(UserPrivacy::getMessagePermission, privacyDTO.getMessagePermission());
        }
        // 执行更新操作
        return userPrivacyMapper.update(null, updateWrapper) > 0;
    }
}
