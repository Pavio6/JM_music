package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.service.SingerInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/singer")
public class AdminSingerInfoController {
    @Resource
    private SingerInfoService singerInfoService;

    /**
     * 分页查询或通过名称模糊查询歌手信息
     * @param singerQry 分页参数
     * @return singerVo
     */
    @GetMapping("/page")
    public Result<IPage<SingerVo>> getSingersByPage(SingerQry singerQry) {
        return Result.success(singerInfoService.getSingersByPage(singerQry));
    }

    /**
     * 添加或更新歌手
     * @param singerInfo 歌手信息
     * @return Result<Boolean>
     */
    @PostMapping("/addOrUpdate")
    public Result<Boolean> addOrUpdate(@RequestBody SingerInfo singerInfo) {
        // id 存在则更新 否则添加
        return Result.success(singerInfoService.saveOrUpdate(singerInfo));
    }


}
