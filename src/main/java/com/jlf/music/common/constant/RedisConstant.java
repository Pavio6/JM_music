package com.jlf.music.common.constant;

/**
 * Redis常量
 */
public class RedisConstant {
    // COMMON
    /**
     * 验证码前缀 有效期
     */
    public static final Integer LOGIN_CAPTCHA_TTL_SEC = 180;
    public static final String ADMIN_LOGIN_PREFIX = "jm:login:captcha:";
    // ADMIN
    public static final String ADMIN_LOGIN_KEY = "admin:login:token:"; // 存储管理员信息
    private static final String OVERVIEW_PREFIX = "admin:overview:";
    public static final String STATISTICS_KEY = OVERVIEW_PREFIX + "statistics";
    public static final String SONG_COUNT_KEY = "song_count";
    public static final String SINGER_COUNT_KEY = "singer_count";
    public static final String ALBUM_COUNT_KEY = "album_count";
    public static final String USER_COUNT_KEY = "user_count";
    public static final Integer CACHE_TIME = 180; // 缓存180分钟

    // USER
    public static final String USER_LOGIN_KEY = "user:login:token:"; // 存储用户信息
    public static final Integer USER_LOGIN_CODE_RESEND_TTL_SEC = 60;
    public static final Integer USER_LOGIN_CODE_TTL_SEC = 60 * 10;
    public static final String COMMENT_LIKED_KEY_PREFIX = "user:comment:liked:"; // 用户对评论点赞
    public static final String FOLLOWS_USER_KEY_PREFIX = "follows:user:"; // 用户关注用户
    public static final String FOLLOWS_SINGER_KEY_PREFIX = "follows:singer:"; // 用户关注歌手
    public static final String FOLLOWERS_USER_KEY_PREFIX = "followers:user:"; // 用户的粉丝
    public static final String FOLLOWERS_SINGER_KEY_PREFIX = "followers:singer:"; // 歌手的粉丝
    public static final String FAVORITE_SONG_KEY_PREFIX = "favorite:song:"; // 用户喜欢的歌曲
    public static final String FAVORITE_PLAYLIST_KEY_PREFIX = "favorite:singer:"; // 用户喜欢的歌单
    public static final String FAVORITE_ALBUM_KEY_PREFIX = "favorite:album:"; // 用户喜欢的专辑
    /**
     * 用户每日听歌时长
     */
    public static final String DAILY_DURATION_KEY_PREFIX = "user:daily:duration:";
    /**
     * 用户最常听的歌手
     */
    public static final String MOST_LISTENED_SINGERS_KEY_PREFIX = "user:most:listened:singers:";
    /**
     * 用户歌曲排名
     */
    public static final String SONG_RANKING_KEY_PREFIX = "user:song:ranking:";


}
