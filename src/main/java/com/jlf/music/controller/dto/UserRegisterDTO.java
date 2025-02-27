package com.jlf.music.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
@SuppressWarnings("all")
/**
 * 用户注册DTO
 */
/**
 * @NotBlank 字符串非空（至少一个非空格字符）
 * @NotEmpty 集合、数组、字符串非空（但允许全空格字符串）
 * @NotNull 对象不能为 null
 * @Min(value) 数值最小值
 * @Max(value) 数值最大值
 * @Size(min, max)	字符串、集合或数组的长度范围
 * @Email 符合邮箱格式
 * @Pattern(regexp) 正则表达式匹配
 * @Valid 级联验证（用于嵌套对象）
 */
@Data
@Accessors(chain = true)
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需为3-20个字符")
    private String userName;
    @NotBlank(message = "密码不能为空")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "密码至少8位，需包含字母和数字"
    )
    private String userPass;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String userEmail;
    @Size(max = 500, message = "简介最多500字")
    private String userBio;
    @Min(value = 0, message = "性别参数不合法")
    @Max(value = 1, message = "性别参数不合法")
    private Integer userSex;
    @Past(message = "生日必须为过去日期")
    private LocalDate userBirth;
    // 用户头像
    private MultipartFile userAvatar;
}
