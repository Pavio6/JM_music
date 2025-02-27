package com.jlf.music.controller.web.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.TypeVo;
import com.jlf.music.entity.TypeInfo;
import com.jlf.music.service.TypeInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/type")
public class AdminTypeInfoController {
    @Resource
    private TypeInfoService typeInfoService;

    /**
     * 查询所有类型或通过名称模糊查询
     * @return 所有类型
     */
    @GetMapping("/get")
    public Result<List<TypeVo>> getAllTypes(@RequestParam String typeName) {
        LambdaQueryWrapper<TypeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(typeName), TypeInfo::getTypeName, typeName);
        List<TypeInfo> list = typeInfoService.list(wrapper);
        return Result.success(CopyUtils.classCopyList(list, TypeVo.class));
    }

    /**
     * 添加或更新类型
     */
    @PostMapping("/addOrUpdate")
    public Result<Boolean> addOrUpdateType(@RequestBody TypeInfo typeInfo) {
        return Result.success(typeInfoService.saveOrUpdate(typeInfo));
    }
}
