package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private String userName;
    private String userEmail;
    private String userBio;
    private LocalDate userBirth;
    private Integer userSex;
    private Integer userStatus;
}
