package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.UserPrivacyDTO;
import com.jlf.music.service.UserPrivacyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 用户隐私控制层
 */
@RestController
@RequestMapping("/api/user/privacy")
public class UserPrivacyController {
    @Resource
    private UserPrivacyService userPrivacyService;
    /**
     * 更新用户个人隐私设置
     */
    @PutMapping("/update")
    public Result<Boolean> updatePrivacySettings(@RequestBody UserPrivacyDTO privacyDTO) {
        return Result.success(userPrivacyService.updatePrivacySettings(privacyDTO));
    }

}
