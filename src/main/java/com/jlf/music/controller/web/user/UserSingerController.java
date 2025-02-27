package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.service.SingerInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/singer")
public class UserSingerController {
    @Resource
    private SingerInfoService singerInfoService;

    /**
     * 分页获取歌手列表(支持区域分类查询)
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    @GetMapping("/page")
    public Result<IPage<SingerBasicInfoVo>> getSingerPageByRegion(SingerByRegionQry singerByRegionQry) {
        return Result.success(singerInfoService.getSingerPageByRegion(singerByRegionQry));
    }
}
