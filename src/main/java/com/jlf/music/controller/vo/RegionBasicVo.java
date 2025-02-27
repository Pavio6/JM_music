package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegionBasicVo {
    private Integer regionId;
    private String regionName;
}
