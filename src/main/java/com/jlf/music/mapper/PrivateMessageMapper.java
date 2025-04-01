package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @Author JLF
 * @Description xxx
 * @Date 2025/3/16 17:13
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {
    @Select("SELECT mi.*, " +
            "s.user_name as sender_name, s.user_avatar as sender_avatar, " +
            "r.user_name as receiver_name, r.user_avatar as receiver_avatar " +
            "FROM message_info mi " +
            "JOIN sys_user s ON mi.sender_id = s.user_id " +
            "JOIN sys_user r ON mi.receiver_id = r.user_id " +
            "WHERE (mi.sender_id = #{userId} AND mi.receiver_id = #{otherUserId}) " +
            "OR (mi.sender_id = #{otherUserId} AND mi.receiver_id = #{userId}) " +
            "ORDER BY mi.create_time DESC")
    IPage<Map<String, Object>> getConversationMessages(
            Page<Map<String, Object>> page,
            @Param("userId") Long userId,
            @Param("otherUserId") Long otherUserId);

    @Select("SELECT mi.* FROM message_info mi " +
            "WHERE mi.receiver_id = #{userId} AND mi.is_read = 0 " +
            "ORDER BY mi.create_time")
    List<PrivateMessage> getUnreadMessages(@Param("userId") Long userId);

    @Update("UPDATE message_info SET status = 'RECALLED' " +
            "WHERE id = #{messageId} AND sender_id = #{userId} " +
            "AND status != 'RECALLED' " +
            "AND create_time > DATE_SUB(NOW(), INTERVAL 2 MINUTE)")
    int recallMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);
}
