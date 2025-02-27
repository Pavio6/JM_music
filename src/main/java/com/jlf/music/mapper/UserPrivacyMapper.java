package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.controller.dto.UserPrivacyDTO;
import com.jlf.music.entity.UserPrivacy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserPrivacyMapper extends BaseMapper<UserPrivacy> {
    /**
     * set标签：用于动态生成set子句，会自动处理逗号
     *
     */

    @Update({
            "<script>",
            "UPDATE user_privacy",
            "<set>",
            "<if test='privacyDTO.profileVisibility != null'>profile_visibility = #{privacyDTO.profileVisibility},</if>",
            "<if test='privacyDTO.followersVisibility != null'>followers_visibility = #{privacyDTO.followersVisibility},</if>",
            "<if test='privacyDTO.followingVisibility != null'>following_visibility = #{privacyDTO.followingVisibility},</if>",
            "<if test='privacyDTO.playlistVisibility != null'>playlist_visibility = #{privacyDTO.playlistVisibility},</if>",
            "<if test='privacyDTO.messagePermission != null'>message_permission = #{privacyDTO.messagePermission}</if>",
            "</set>",
            "WHERE user_id = #{userId}",
            "</script>"
    })
    Integer updatePrivacyByUserId(@Param("userId") Long userId, @Param("privacyDTO") UserPrivacyDTO privacyDTO);
}
