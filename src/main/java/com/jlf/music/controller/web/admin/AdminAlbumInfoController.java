package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.service.AlbumInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/album")
public class AdminAlbumInfoController {
    @Resource
    private AlbumInfoService albumInfoService;


    /**
     * 分页或通过名称模糊查询album
     *
     * @param albumQry 专辑名称和分页参数
     * @return AlbumVo
     */
    @GetMapping("/page")
    public Result<IPage<AlbumVo>> getAlbumsByPage(AlbumQry albumQry) {
        return Result.success(albumInfoService.getAlbumsByPage(albumQry));
    }

    /**
     * 根据专辑ID获取专辑中所有歌曲
     *
     * @param albumId 专辑ID
     * @return songVo
     */
    @GetMapping("/{albumId}")
    public Result<List<SongBasicInfoVo>> getAlbumWithSongs(@PathVariable Long albumId) {
        return Result.success(albumInfoService.getAlbumWithSongs(albumId));
    }

}
