package com.jlf.music.controller.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UserUpdateDTO {
    private String userName;
    private String userEmail;
    private String userBio;

    private LocalDate userBirth;
    private Integer userSex;
}
