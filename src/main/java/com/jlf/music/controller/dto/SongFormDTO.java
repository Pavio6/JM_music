package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author JLF
 * @date 2025/3/30 17:21
 * @description xxx
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SongFormDTO {
    private String songName;
    private LocalTime songDuration;
    private LocalDate songReleaseDate;
    private Long singerId;
    private Long albumId;
}
