package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.entity.MessageConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Author JLF
 * @Description xxx
 * @Date 2025/3/16 17:15
 */
@Mapper
public interface MessageConversationMapper extends BaseMapper<MessageConversation> {
    @Select("SELECT mc.*, u.username as other_user_name, u.avatar as other_user_avatar, " +
            "pm.content as last_message, pm.create_time as last_message_time, " +
            "pm.status as last_message_status " +
            "FROM message_conversation mc " +
            "JOIN user u ON mc.other_user_id = u.id " +
            "LEFT JOIN private_message pm ON mc.last_message_id = pm.id " +
            "WHERE mc.user_id = #{userId} " +
            "ORDER BY mc.is_top DESC, mc.last_update_time DESC")
    IPage<Map<String, Object>> getUserConversations(
            Page<Map<String, Object>> page,
            @Param("userId") Long userId);
}
