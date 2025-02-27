package com.jlf.music.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "密码至少8位，需包含字母和数字"
    )
    private String newPassword;
}
