package com.jlf.music.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.SingerFormDTO;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.controller.vo.SingerDetailInfoVo;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.RegionInfo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.entity.UserFollow;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.RegionInfoMapper;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.mapper.UserFollowMapper;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SingerInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 分页查询singer
     *
     * @param singerQry 分页参数
     * @return singerVo
     */
    @Override
    public IPage<SingerVo> getSingersByPage(SingerQry singerQry) {
        Page<SingerInfo> page = new Page<>(singerQry.getPageNum(), singerQry.getPageSize());
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
            throw new ServiceException("没有该歌手");
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
        String count = (String) stringRedisTemplate.opsForHash().get(FOLLOWERS_COUNT, singerId.toString());
        // 命中
        if (count != null) {
            vo.setFollowerCount(Long.parseLong(count));
        } else {
            // 未命中 从数据库中查询
            Long newCount = userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>()
                    .eq(UserFollow::getFollowType, 1)
                    .eq(UserFollow::getFollowedId, singerId));
            // 如果数据库查询结果为空，设置默认值（避免缓存穿透）
            if (newCount == null) {
                newCount = 0L;
            }
            // 存入redis
            stringRedisTemplate.opsForHash().put(FOLLOWERS_COUNT, singerId.toString(), newCount.toString());
            // 有效期30分钟
            stringRedisTemplate.expire(FOLLOWERS_COUNT, 30, TimeUnit.MINUTES);
            vo.setFollowerCount(newCount);
        }
        return vo;
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
}
