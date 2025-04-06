package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.SingerFormDTO;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerOptionVo;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.RegionInfoService;
import com.jlf.music.service.SingerInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/singer")
public class AdminSingerInfoController {
    @Resource
    private SingerInfoService singerInfoService;
    @Resource
    private RegionInfoService regionInfoService;

    /**
     * 分页查询或通过名称模糊查询歌手信息
     *
     * @param singerQry 分页参数
     * @return singerVo
     */
    @GetMapping("/list")
    public Result<IPage<SingerVo>> getSingersByPage(SingerQry singerQry) {
        return Result.success(singerInfoService.getSingersByPage(singerQry));
    }

    /**
     * 更新歌手
     */
    @PutMapping("/{singerId}")
    public Result<Boolean> updateSinger(@RequestPart(value = "singerFormDTO", required = false) SingerFormDTO singerFormDTO,
                                        @RequestPart(value = "singerAvatar", required = false) MultipartFile avatarFile,
                                        @PathVariable Long singerId) {
        return Result.success(singerInfoService.updateSingerById(singerId, singerFormDTO, avatarFile));
    }

    /**
     * 获取歌手详情
     */
    @GetMapping("/{singerId}")
    public Result<SingerVo> getSingerDetail(@PathVariable Long singerId) {
        SingerInfo singerInfo = singerInfoService.getById(singerId);
        if (singerInfo == null) {
            throw new ServiceException("歌手不存在");
        }
        SingerVo singerVo = new SingerVo();
        CopyUtils.classCopy(singerInfo, singerVo);
        singerVo.setRegionName(regionInfoService.getById(singerVo.getRegionId()).getRegionName());
        return Result.success(singerVo);
    }

    /**
     * 获取歌手下拉菜单
     */
    @GetMapping("/options")
    public Result<List<SingerOptionVo>> getSingerOptions() {
        // 获取歌手信息列表
        List<SingerInfo> singerInfoList = singerInfoService.list();
        // 使用 Stream 流将 SingerInfo 转换为 SingerOptionVo
        List<SingerOptionVo> singerOptionVoList = singerInfoList.stream()
                .map(singerInfo -> {
                    SingerOptionVo vo = new SingerOptionVo();
                    vo.setSingerId(singerInfo.getSingerId());
                    vo.setSingerName(singerInfo.getSingerName());
                    return vo;
                })
                .toList();
        return Result.success(singerOptionVoList);
    }
    /**
     * 新增歌手
     */
    @PostMapping
    public Result<Boolean> addSinger(@RequestPart(value = "singerFormDTO", required = false) SingerFormDTO singerFormDTO,
                                        @RequestPart(value = "singerAvatar", required = false) MultipartFile avatarFile) {
        return Result.success(singerInfoService.addSinger(singerFormDTO, avatarFile));
    }
}
