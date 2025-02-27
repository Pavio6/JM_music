package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.AlbumSearchVo;
import com.jlf.music.controller.vo.SearchResultVo;
import com.jlf.music.controller.vo.SingerSearchVo;
import com.jlf.music.controller.vo.SongSimpleInfoVo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/search")
public class UserSearchController {
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private AlbumInfoMapper albumInfoMapper;

    @GetMapping
    public Result<SearchResultVo> search(@RequestParam String keyword,
                                         @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(new SearchResultVo(searchSongs(keyword, limit), searchSingers(keyword, limit), searchAlbums(keyword, limit)));
    }

    /**
     * 搜索歌曲
     */
    private List<SongSimpleInfoVo> searchSongs(String keyword, Integer limit) {
        return songInfoMapper.searchSongs(keyword, limit);
    }

    /**
     * 搜索歌手
     */
    private List<SingerSearchVo> searchSingers(String keyword, Integer limit) {
        Page<SingerInfo> page = new Page<>(1, limit);
        return singerInfoMapper.selectPage(page,
                        new LambdaQueryWrapper<SingerInfo>().like(SingerInfo::getSingerName, keyword))
                .getRecords()
                .stream()
                .map(singerInfo -> new SingerSearchVo(
                        singerInfo.getSingerId(),
                        singerInfo.getSingerName(),
                        singerInfo.getSingerAvatar()
                ))
                .toList();
    }

    /**
     * 搜索专辑
     */
    private List<AlbumSearchVo> searchAlbums(String keyword, Integer limit) {
        return albumInfoMapper.searchAlbums(keyword, limit);
    }

}
