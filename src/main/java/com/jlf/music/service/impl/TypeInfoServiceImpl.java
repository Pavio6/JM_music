package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.entity.TypeInfo;
import com.jlf.music.mapper.TypeInfoMapper;
import com.jlf.music.service.TypeInfoService;
import org.springframework.stereotype.Service;

@Service
public class TypeInfoServiceImpl extends ServiceImpl<TypeInfoMapper, TypeInfo> implements TypeInfoService {

}
