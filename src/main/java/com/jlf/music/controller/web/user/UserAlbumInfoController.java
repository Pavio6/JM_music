package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.entity.TypeInfo;
import com.jlf.music.service.AlbumInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 专辑controller
 * @Author JLF
 * @Date 2025/4/28 14:26
 * @Version 1.0
 */
@RequestMapping("/api/user/album")
@RestController
public class UserAlbumInfoController {

    @Resource
    private AlbumInfoService albumInfoService;

    /**
     * 获取专辑类型
     */
    @GetMapping("/type")
    public Result<List<TypeInfo>> getAlbumType() {
        return Result.success(albumInfoService.getAlbumType());
    }

    /**
     * 获取专辑列表 可以通过类型查询
     */
    @GetMapping("/list")
    public Result<IPage<AlbumVo>> getAlbumList(AlbumQry albumQry) {
        return Result.success(albumInfoService.getAlbumsByPage(albumQry));
    }

    /**
     * 获取专辑详情
     */
    @GetMapping("/{albumId}/detail")
    public Result<AlbumVo> getAlbumDetailByAlbumId(@PathVariable Long albumId) {
        return Result.success(albumInfoService.getAlbumDetailByAlbumId(albumId));
    }

}
