package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.entity.CarouselInfo;
import com.jlf.music.service.CarouselInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 轮播信息控制器
 * @Author JLF
 * @Date 2025/4/29 17:13
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user/carousel")
public class UserCarouselInfoController {
    @Resource
    private CarouselInfoService carouselInfoService;

    /**
     * 获取轮播图信息
     *
     * @return 轮播图列表
     */
    @GetMapping("/list")
    public Result<List<CarouselInfo>> getAllCarousels() {
        return Result.success(carouselInfoService.getAllCarousels());
    }
}
