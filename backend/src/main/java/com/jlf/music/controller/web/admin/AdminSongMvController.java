package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.SongMvDTO;
import com.jlf.music.controller.qry.SongMvQry;
import com.jlf.music.controller.vo.MvListVo;
import com.jlf.music.service.SongMvService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @Description
 * @Author JLF
 * @Date 2025/4/15 15:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/admin/mv")
public class AdminSongMvController {
    @Resource
    private SongMvService songMvService;

    /**
     * 分页获取mv列表
     */
    @GetMapping("/list")
    public Result<IPage<MvListVo>> page(SongMvQry songMvQry) {
        return Result.success(songMvService.getMvList(songMvQry));
    }

    /**
     * 编辑mv
     */
    @PutMapping("/{mvId}")
    public Result<Boolean> update(@PathVariable Long mvId,
                                  @RequestPart("songMvDTO") SongMvDTO songMvDTO,
                                  @RequestPart(value = "mvFilePath480p", required = false) MultipartFile mvFilePath480p,
                                  @RequestPart(value = "mvFilePath720p", required = false) MultipartFile mvFilePath720p,
                                  @RequestPart(value = "mvFilePath1080p", required = false) MultipartFile mvFilePath1080p) {

        return Result.success(songMvService.updateSongMv(mvId, songMvDTO, mvFilePath480p, mvFilePath720p, mvFilePath1080p));
    }
    /**
     * 删除mv
     */
    @DeleteMapping("/{mvId}")
    public Result<Boolean> delete(@PathVariable Long mvId) {
        return Result.success(songMvService.removeSongMvById(mvId));
    }

}
