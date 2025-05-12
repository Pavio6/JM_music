package com.jlf.music.controller.web.admin;

import ch.qos.logback.core.joran.spi.ElementSelector;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.OptionalVo;
import com.jlf.music.entity.CarouselInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.PlaylistInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.CarouselInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/29 17:16
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/admin/carousel")
public class AdminCarouselInfoController {
    @Resource
    private CarouselInfoService carouselInfoService;


    /**
     * 添加轮播图信息
     *
     * @param carouselInfo 轮播图对象
     * @param imageFile    图片文件
     * @return 添加成功/失败
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestPart("carouselInfo") CarouselInfo carouselInfo,
                               @RequestPart(value = "imageFile", required = true) MultipartFile imageFile) {

        return Result.success(carouselInfoService.add(carouselInfo, imageFile));
    }

    /**
     * 根据类型获取optional
     *
     * @param type 类型
     * @return 下拉框列表
     */
    @GetMapping("/optional")
    public Result<List<OptionalVo>> getOptional(@RequestParam String type) {
        return Result.success(carouselInfoService.getOptional(type));
    }

}
