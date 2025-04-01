package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SongMvService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: JLF
 * @Description: 歌曲视频
 * @Date 2025/3/19 11:55
 */
@RestController
@RequestMapping("/api/user/mv")
public class UserSongMvController {
    @Resource
    private SongMvService songMvService;

    /**
     * 根据mvId获取MV详细信息
     *
     * @param mvId 视频id
     * @return SongMvDetailVo
     */
    @GetMapping("/detail/{mvId}")
    public Result<SongMvDetailVo> getMvDetail(@PathVariable Long mvId) {
        if (songMvService.getById(mvId) == null) {
            throw new ServiceException("不存在该视频");
        }
        return Result.success(songMvService.getMvDetail(mvId));
    }

}
