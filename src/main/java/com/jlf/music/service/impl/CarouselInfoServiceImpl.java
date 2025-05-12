package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.Result;
import com.jlf.music.common.constant.Constant;
import com.jlf.music.common.enumerate.CarouselType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.vo.OptionalVo;
import com.jlf.music.entity.CarouselInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.CarouselInfoMapper;
import com.jlf.music.mapper.PlaylistInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.CarouselInfoService;
import com.jlf.music.service.FileService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/29 17:14
 * @Version 1.0
 */
@Service
public class CarouselInfoServiceImpl extends ServiceImpl<CarouselInfoMapper, CarouselInfo>
        implements CarouselInfoService {
    @Resource
    private FileService fileService;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private AlbumInfoMapper albumInfoMapper;
    @Resource
    private PlaylistInfoMapper playlistInfoMapper;
    public static final String URL_REGEX = "^(https?://)?"
            + "([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}"
            + "(:[0-9]{1,5})?"
            + "(/[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;=]*)?$";

    /**
     * 获取轮播图信息
     *
     * @return 轮播图列表
     */
    @Override
    public List<CarouselInfo> getAllCarousels() {
        LambdaQueryWrapper<CarouselInfo> wrapper = new LambdaQueryWrapper<CarouselInfo>()
                .eq(CarouselInfo::getStatus, Constant.INTEGER_ONE) // 状态为上线
                .le(CarouselInfo::getStartTime, new Date()) // <=
                .ge(CarouselInfo::getEndTime, new Date()) // >=
                .orderByAsc(CarouselInfo::getSortOrder); // 升序
        return this.list(wrapper);
    }

    /**
     * 添加轮播图信息
     *
     * @param carouselInfo 轮播图对象
     * @param imageFile    图片文件
     * @return 添加成功/失败
     */
    @Override
    public Boolean add(CarouselInfo carouselInfo, MultipartFile imageFile) {
        // 获取一条轮播图信息 新的轮播信息默认为最大的sort值+1
        CarouselInfo maxOrderCarousel = this.getOne(new LambdaQueryWrapper<CarouselInfo>()
                        .select(CarouselInfo::getSortOrder)
                        .orderByDesc(CarouselInfo::getSortOrder) // 降序
                        .last("LIMIT 1"),
                false);
        int maxOrder = maxOrderCarousel != null ? maxOrderCarousel.getSortOrder() : 0;
        // 设置排序顺序
        carouselInfo.setSortOrder(maxOrder + 1);
        // 设置状态
        carouselInfo.setStatus(Constant.INTEGER_ONE);
        // 获取 targetType 并校验是否合法
        Integer targetType = carouselInfo.getTargetType();
        if (targetType == null || !CarouselType.isValid(targetType)) {
            throw new ServiceException("非法的 targetType 类型");
        }
        // 根据枚举值获取枚举对象
        CarouselType type = CarouselType.fromCode(targetType);
        // 根据类型判断 target_id 或 external_url 是否必填
        if (type.isMedia()) {
            if (carouselInfo.getTargetId() == null) {
                throw new ServiceException("歌曲/歌单/专辑类型必须提供 target_id");
            }
        } else if (type.isArticleOrExternal()) {
            if (carouselInfo.getExternalUrl() == null || carouselInfo.getExternalUrl().trim().isEmpty()) {
                throw new ServiceException("文章/外部链接类型必须提供 external_url");
            }
            // 判断链接格式是否合法
            String url = carouselInfo.getExternalUrl().trim();
            Pattern pattern = Pattern.compile(URL_REGEX);
            Matcher matcher = pattern.matcher(url);
            if (!matcher.matches()) {
                throw new ServiceException("外部链接格式不合法，请提供有效的 URL");
            }
        }
        if (carouselInfo.getStartTime() == null) {
            throw new ServiceException("轮播图必须有开始时间");
        }
        // 校验 endTime 是否已过期
        if (carouselInfo.getEndTime() != null && carouselInfo.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException("结束时间不能早于当前时间");
        }
        // 保存轮播图到minio中
        if (imageFile.isEmpty()) {
            throw new ServiceException("轮播图图片未上传");
        }
        // 上传轮播图信息到minio中
        String imageUrl = fileService.uploadImageFile(imageFile, UploadFileType.CAROUSEL_IMAGE);
        carouselInfo.setImageUrl(imageUrl);
        Long userId = SecurityUtils.getUserId();
        carouselInfo.setCreateBy(userId);
        return this.save(carouselInfo);
    }

    /**
     * 根据类型获取optional
     *
     * @param type 类型
     * @return 下拉框列表
     */
    @Override
    public List<OptionalVo> getOptional(String type) {
        return switch (type) {
            case "SONG" -> songInfoMapper.selectList(null).stream()
                    .map(song -> new OptionalVo().setId(song.getSongId()).setName(song.getSongName()))
                    .toList();
            case "PLAYLIST" -> playlistInfoMapper.selectList(null).stream()
                    .map(playlistInfo -> new OptionalVo().setId(playlistInfo.getPlaylistId()).setName(playlistInfo.getPlaylistName()))
                    .toList();
            case "ALBUM" -> albumInfoMapper.selectList(null).stream()
                    .map(albumInfo -> new OptionalVo().setId(albumInfo.getAlbumId()).setName(albumInfo.getAlbumName()))
                    .toList();
            default -> throw new ServiceException("类型不匹配");
        };
    }
}
