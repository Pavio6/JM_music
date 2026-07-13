package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.UserConnection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author JLF
 * @Description xxx
 * @Date 2025/3/16 17:15
 */
@Mapper
public interface UserConnectionMapper extends BaseMapper<UserConnection> {
    @Select("SELECT is_online FROM user_connection WHERE user_id = #{userId}")
    Boolean isUserOnline(@Param("userId") Long userId);

    @Select("SELECT user_id FROM user_connection WHERE session_id = #{sessionId}")
    Long getUserIdBySessionId(@Param("sessionId") String sessionId);
}
