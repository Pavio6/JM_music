package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.AlbumFormDTO;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumOptionVo;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.AlbumInfo;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.TypeInfoMapper;
import com.jlf.music.service.AlbumInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.List;


@RestController
@RequestMapping("/api/admin/album")
public class AdminAlbumInfoController {
    @Resource
    private AlbumInfoService albumInfoService;
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private TypeInfoMapper typeInfoMapper;

    /**
     * 分页或根据条件模糊查询album
     *
     * @param albumQry 模糊条件和分页参数
     * @return AlbumVo
     */
    @GetMapping("/list")
    public Result<IPage<AlbumVo>> getAlbumsByPage(AlbumQry albumQry) {
        return Result.success(albumInfoService.getAlbumsByPage(albumQry));
    }

    /**
     * 根据albumId获取专辑详情
     */
    @GetMapping("/{albumId}")
    public Result<AlbumVo> getAlbumDetailById(@PathVariable("albumId") Long albumId) {
        AlbumInfo albumInfo = albumInfoService.getById(albumId);
        AlbumVo albumVo = CopyUtils.classCopy(albumInfo, AlbumVo.class);
        return Result.success(albumVo.setSingerName(singerInfoMapper.selectById(albumInfo.getSingerId()).getSingerName())
                .setTypeName(typeInfoMapper.selectById(albumInfo.getTypeId()).getTypeName())
                .setCreateTime(albumInfo.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .setUpdateTime(albumInfo.getUpdateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }

    /**
     * 根据专辑ID获取专辑中所有歌曲
     *
     * @param albumId 专辑ID
     * @return songVo
     */
    @GetMapping("/songs/{albumId}")
    public Result<List<SongBasicInfoVo>> getAlbumWithSongs(@PathVariable Long albumId) {
        return Result.success(albumInfoService.getAlbumWithSongs(albumId));
    }

    /**
     * 获取专辑下拉菜单
     */
    @GetMapping("/options")
    public Result<List<AlbumOptionVo>> getSingerOptions(@RequestParam Long singerId) {
        List<AlbumInfo> list = albumInfoService.list(new LambdaQueryWrapper<AlbumInfo>()
                .eq(AlbumInfo::getSingerId, singerId));
        List<AlbumOptionVo> albumOptionVoList = list.stream()
                .map(albumInfo -> new AlbumOptionVo()
                        .setAlbumId(albumInfo.getAlbumId())
                        .setAlbumName(albumInfo.getAlbumName())
                        .setSignerId(singerId))
                .toList();
        return Result.success(albumOptionVoList);
    }

    /**
     * 更新专辑
     */
    @PutMapping("/{albumId}")
    public Result<Boolean> updateAlbum(@RequestPart("albumCoverFile") MultipartFile albumCoverFile,
                                       @RequestPart("albumFormDTO") AlbumFormDTO albumFormDTO,
                                       @PathVariable Long albumId) {
        return Result.success(albumInfoService.updateAlbum(albumCoverFile, albumFormDTO, albumId));
    }
    /**
     * 新增专辑
     */
    @PostMapping
    public Result<Boolean> addAlbum(@RequestPart("albumFormDTO") AlbumFormDTO albumFormDTO,
                                    @RequestPart("albumCoverFile") MultipartFile albumCoverFile) {
        return Result.success(albumInfoService.addAlbum(albumFormDTO, albumCoverFile));
    }
    /**
     * 删除专辑
     */
    @DeleteMapping("/{albumId}")
    public Result<Boolean> deleteAlbum(@PathVariable Long albumId) {
        return Result.success(albumInfoService.deleteAlbum(albumId));
    }
}
