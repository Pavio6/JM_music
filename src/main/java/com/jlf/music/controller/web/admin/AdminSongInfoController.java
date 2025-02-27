package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/song")
public class AdminSongInfoController {
    @Resource
    private SongInfoService songInfoService;

    /**
     * 分页或通过名称模糊查询歌曲信息
     */
    @GetMapping("/page")
    public Result<IPage<SongBasicInfoVo>> getSongsByPage(SongQry songQry) {
        return Result.success(songInfoService.getSongsByPage(songQry));
    }


}
