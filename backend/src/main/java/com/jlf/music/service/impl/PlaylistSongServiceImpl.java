package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.entity.PlaylistSong;
import com.jlf.music.mapper.PlaylistSongMapper;
import com.jlf.music.service.PlaylistSongService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong>
        implements PlaylistSongService {
}
