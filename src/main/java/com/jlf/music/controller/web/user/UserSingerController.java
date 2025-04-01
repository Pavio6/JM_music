package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.controller.vo.SingerDetailInfoVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.service.SingerInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/singer")
public class UserSingerController {
    @Resource
    private SingerInfoService singerInfoService;

    /**
     * 分页获取歌手列表(支持区域分类查询)
     *
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    @GetMapping("/page")
    public Result<IPage<SingerBasicInfoVo>> getSingerPageByRegion(SingerByRegionQry singerByRegionQry) {
        return Result.success(singerInfoService.getSingerPageByRegion(singerByRegionQry));
    }


    /**
     * 根据歌手id获取热门歌曲列表信息
     * 根据歌曲播放量排序
     *
     * @param singerId 歌手id
     */
    @GetMapping("/searchBySingerId")
    public Result<List<SongBasicInfoVo>> searchBySingerId(@RequestParam Long singerId) {
        return Result.success(singerInfoService.searchBySingerId(singerId));
    }

    /**
     * 获取歌手详细信息
     *
     * @param singerId 歌手id
     */
    @GetMapping("/{singerId}")
    public Result<SingerDetailInfoVo> getSingerDetailById(@PathVariable Long singerId) {
        return Result.success(singerInfoService.getSingerDetailById(singerId));
    }
}
