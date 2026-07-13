package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.RegionBasicVo;
import com.jlf.music.service.RegionInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/region")
public class UserRegionController {
    @Resource
    private RegionInfoService regionInfoService;

    /**
     * 获取地域列表
     * @return 地域list
     */
    @GetMapping("/list")
    public Result<List<RegionBasicVo>> getRegionList() {
        return Result.success(regionInfoService.getRegionList());
    }
}
