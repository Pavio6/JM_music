package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/15 16:47
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SongMvDTO {
    private String mvBio;
    private LocalDate mvReleaseDate;
}
