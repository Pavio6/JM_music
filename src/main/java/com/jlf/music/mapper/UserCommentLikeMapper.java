package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.UserCommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author JLF
 * @Date 2025/5/14 11:59
 * @Version 1.0
 */
@Mapper
public interface UserCommentLikeMapper extends BaseMapper<UserCommentLike> {



}
