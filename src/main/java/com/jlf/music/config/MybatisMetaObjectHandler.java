package com.jlf.music.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jlf.music.common.constant.Constant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

// MetaObjectHandler接口 该接口用于处理元对象的填充操作
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动为createTime等字段 添加当前时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "followTime", Date.class, new Date()); // 关注时间
        this.strictInsertFill(metaObject, "collectionTime", Date.class, new Date()); // 收藏时间
        this.strictInsertFill(metaObject, "deleteFlag", Integer.class, Constant.INTEGER_ZERO);
        this.strictInsertFill(metaObject, "userState", Integer.class, Constant.INTEGER_ONE);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动为updateTime字段 修改为当前时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

    }
}
