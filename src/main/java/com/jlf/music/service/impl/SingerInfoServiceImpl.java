package com.jlf.music.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.FollowTargetType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.SingerFormDTO;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.*;
import com.jlf.music.entity.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SingerInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.net.ServletConnectionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jlf.music.common.constant.RedisConstant.FOLLOWERS_COUNT;

@Service
public class SingerInfoServiceImpl extends ServiceImpl<SingerInfoMapper, SingerInfo>
        implements SingerInfoService {
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private RegionInfoMapper regionInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserFollowMapper userFollowMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private FileService fileService;
    @Autowired
    private AlbumInfoMapper albumInfoMapper;

    /**
     * 分页查询singer
     *
     * @param singerQry 分页参数
     * @return singerVo
     */
    @Override
    public IPage<SingerVo> getSingersByPage(SingerQry singerQry) {
        Page<SingerVo> page = new Page<>(singerQry.getPageNum(), singerQry.getPageSize());
        return singerInfoMapper.getSingersByPage(page, singerQry);
    }

    /**
     * 通过区域分页获取歌手列表
     *
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    @Override
    public IPage<SingerBasicInfoVo> getSingerPageByRegion(SingerByRegionQry singerByRegionQry) {
        Page<SingerInfo> page = new Page<>(singerByRegionQry.getPageNum(), singerByRegionQry.getPageSize());
        // 区域id存在
        if (ObjectUtil.isNotEmpty(singerByRegionQry.getRegionId())) {
            // 判断区域id是否存在
            if (regionInfoMapper.selectById(singerByRegionQry.getRegionId()) == null) {
                throw new ServiceException("不存在该区域名称");
            }
            // 进行分页查询
            page = singerInfoMapper.selectPage(page, new LambdaQueryWrapper<SingerInfo>()
                    .eq(SingerInfo::getRegionId, singerByRegionQry.getRegionId()));
            return CopyUtils.covertPage(page, SingerBasicInfoVo.class);
        }
        return CopyUtils.covertPage(singerInfoMapper.selectPage(page, null), SingerBasicInfoVo.class);
    }

    /**
     * 获取歌手详细信息
     *
     * @param singerId 歌手id
     */
    @Override
    public SingerDetailInfoVo getSingerDetailById(Long singerId) {
        SingerInfo singerInfo = this.getById(singerId);
        if (singerInfo == null) {
            throw new ServiceException("该歌手不存在");
        }
        SingerDetailInfoVo vo = new SingerDetailInfoVo();
        vo.setSingerAvatar(singerInfo.getSingerAvatar())
                .setSingerBio(singerInfo.getSingerBio())
                .setSingerName(singerInfo.getSingerName())
                .setSingerId(singerId);
        // 获取歌手地域名称
        String regionName = regionInfoMapper.selectById(singerInfo.getRegionId()).getRegionName();
        vo.setRegionName(regionName);
        // 获取歌手粉丝数量
        // 从redis中查找
        String count = stringRedisTemplate.opsForValue().get(FOLLOWERS_COUNT + singerId);
        // 命中
        if (count != null) {
            vo.setFollowerCount(Long.parseLong(count));
        } else {
            // 未命中 从数据库中查询
            Long newCount = userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>()
                    .eq(UserFollow::getFollowType, FollowTargetType.SINGER.getValue())
                    .eq(UserFollow::getFollowedId, singerId));
            // 如果数据库查询结果为空，设置默认值（避免缓存穿透）
            if (newCount == null) {
                newCount = 0L;
            }
            // 存入redis
            stringRedisTemplate.opsForValue().set(FOLLOWERS_COUNT + singerId, newCount.toString());
            // 有效期30分钟
            stringRedisTemplate.expire(FOLLOWERS_COUNT + singerId, 30, TimeUnit.MINUTES);
            vo.setFollowerCount(newCount);
        }
        // 获取歌手的热门歌曲
        List<SongBasicInfoVo> songBasicInfoVos = searchBySingerId(singerId);
        vo.setSongs(songBasicInfoVos);
        // 获取歌手的热门专辑
        List<AlbumSearchVo> albumSearchVos = searchAlbumBySingerId(singerId);
        vo.setAlbums(albumSearchVos);
        return vo;
    }

    private List<AlbumSearchVo> searchAlbumBySingerId(Long singerId) {
        List<AlbumInfo> albumInfos = albumInfoMapper.selectList(new LambdaQueryWrapper<AlbumInfo>()
                .eq(AlbumInfo::getSingerId, singerId)
                .last("limit 5"));
        return CopyUtils.classCopyList(albumInfos, AlbumSearchVo.class);
    }

    /**
     * 根据歌手id获取热门歌曲列表信息
     * 根据歌曲播放量排序
     *
     * @param singerId 歌手id
     */
    @Override
    public List<SongBasicInfoVo> searchBySingerId(Long singerId) {
        if (this.getById(singerId) == null) {
            throw new ServiceException("该歌手不存在");
        }
        return songInfoMapper.selectTopSongsBySingerId(singerId);

    }

    /**
     * 更新歌手
     */
    @Override
    @Transactional
    public Boolean updateSingerById(Long singerId, SingerFormDTO singerFormDTO, MultipartFile avatarFile) {
        SingerInfo singerInfo = this.getById(singerId);
        if (singerInfo == null) {
            throw new ServiceException("歌手不存在");
        }
        String singerAvatar = avatarFile != null ? fileService.uploadImageFile(avatarFile, UploadFileType.SINGER_AVATAR) : null;
        // 更新非空字段
        if (singerFormDTO.getSingerName() != null && !singerFormDTO.getSingerName().isBlank()) {
            singerInfo.setSingerName(singerFormDTO.getSingerName());
        }
        if (singerFormDTO.getSingerNat() != null && !singerFormDTO.getSingerNat().isBlank()) {
            singerInfo.setSingerNat(singerFormDTO.getSingerNat());
        }
        if (singerFormDTO.getSingerBio() != null && !singerFormDTO.getSingerBio().isBlank()) {
            singerInfo.setSingerBio(singerFormDTO.getSingerBio());
        }
        if (singerFormDTO.getSingerBirth() != null) {
            singerInfo.setSingerBirth(singerFormDTO.getSingerBirth());
        }
        if (singerFormDTO.getSingerDebutDate() != null) {
            singerInfo.setSingerDebutDate(singerFormDTO.getSingerDebutDate());
        }
        if (singerFormDTO.getSingerSex() != null) {
            singerInfo.setSingerSex(singerFormDTO.getSingerSex());
        }
        if (singerFormDTO.getRegionId() != null) {
            singerInfo.setRegionId(singerFormDTO.getRegionId());
        }
        // 更新文件路径
        if (singerAvatar != null) {
            // 删除之前存放在minio中的文件
            fileService.deleteFile(singerInfo.getSingerAvatar());
            singerInfo.setSingerAvatar(singerAvatar);
        }

        return singerInfoMapper.updateById(singerInfo) > 0;
    }

    /**
     * 新增歌手
     */
    @Override
    public Boolean addSinger(SingerFormDTO singerFormDTO, MultipartFile avatarFile) {
        // 判断参数是否合法
        if (singerFormDTO.getSingerName() == null || singerFormDTO.getSingerName().isBlank()) {
            throw new ServiceException("歌手名称不能为空!");
        }
        if (singerFormDTO.getSingerNat() == null || singerFormDTO.getSingerNat().isBlank()) {
            throw new ServiceException("歌手国籍不能为空!");
        }
        if (singerFormDTO.getSingerBirth() == null) {
            throw new ServiceException("歌手出生日期不能为空!");
        }
        if (singerFormDTO.getSingerBirth().isAfter(LocalDate.now())) {
            throw new ServiceException("出生日期不能晚于当前日期");
        }
        if (singerFormDTO.getSingerDebutDate() != null) {
            if (singerFormDTO.getSingerDebutDate().isAfter(LocalDate.now())) {
                throw new ServiceException("出道日期不能晚于当前日期");
            }
        }
        if (singerFormDTO.getSingerSex() != 0 && singerFormDTO.getSingerSex() != 1) {
            throw new ServiceException("性别不合法");
        }
        SingerInfo singerInfo = new SingerInfo();
        CopyUtils.classCopy(singerFormDTO, singerInfo);
        String singerAvatar = avatarFile != null ? fileService.uploadImageFile(avatarFile, UploadFileType.SINGER_AVATAR) : null;
        if (singerAvatar != null) {
            singerInfo.setSingerAvatar(singerAvatar);
        }
        return singerInfoMapper.insert(singerInfo) > 0;
    }

    /**
     * 删除歌手
     */
    @Override
    public Boolean deleteSinger(Long singerId) {
        SingerInfo singerInfo = singerInfoMapper.selectById(singerId);
        if (singerInfo == null) {
            throw new ServiceException("歌手不存在，无法删除");
        }
        if (albumInfoMapper.selectCount(new LambdaQueryWrapper<AlbumInfo>()
                .eq(AlbumInfo::getSingerId, singerId)) > 0) {
            throw new ServiceException("无法删除，请先删除歌手关联的专辑信息");
        }
        if (songInfoMapper.selectCount(new LambdaQueryWrapper<SongInfo>()
                .eq(SongInfo::getSingerId, singerId)) > 0) {
            throw new ServiceException("无法删除，请先删除歌手关联的歌曲信息");
        }
        return singerInfoMapper.deleteById(singerId) > 0;
    }
}
