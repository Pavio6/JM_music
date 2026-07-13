package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.vo.OptionalVo;
import com.jlf.music.entity.CarouselInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/29 17:14
 * @Version 1.0
 */
public interface CarouselInfoService extends IService<CarouselInfo> {
    /**
     * 获取轮播图信息
     *
     * @return 轮播图列表
     */
    List<CarouselInfo> getAllCarousels();

    /**
     * 添加轮播图信息
     *
     * @param carouselInfo 轮播图对象
     * @param imageFile    图片文件
     * @return 添加成功/失败
     */
    Boolean add(CarouselInfo carouselInfo, MultipartFile imageFile);

    /**
     * 根据类型获取optional
     *
     * @param type 类型
     * @return 下拉框列表
     */
    List<OptionalVo> getOptional(String type);
}
