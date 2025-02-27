package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.dto.UserPrivacyDTO;
import com.jlf.music.entity.UserPrivacy;
import com.jlf.music.mapper.UserPrivacyMapper;
import com.jlf.music.service.UserPrivacyService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserPrivacyServiceImpl extends ServiceImpl<UserPrivacyMapper, UserPrivacy> implements UserPrivacyService {

    @Resource
    private UserPrivacyMapper userPrivacyMapper;
    /**
     * 修改用户隐私设置
     * @param privacyDTO 隐私dto
     * @return Boolean
     */
    @Override
    public Boolean updatePrivacySettings(UserPrivacyDTO privacyDTO) {
        Long userId = SecurityUtils.getUserId();
        return userPrivacyMapper.updatePrivacyByUserId(userId, privacyDTO) > 0;
    }

    /**
     * 获取用户隐私设置
     *
     * @param id 用户ID
     * @return dto
     */
    @Override
    public UserPrivacyDTO getPrivacySettings(Long id) {
        LambdaQueryWrapper<UserPrivacy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPrivacy::getUserId, id);
        UserPrivacy userPrivacy = userPrivacyMapper.selectOne(queryWrapper);
        return CopyUtils.classCopy(userPrivacy, UserPrivacyDTO.class);
    }
}
