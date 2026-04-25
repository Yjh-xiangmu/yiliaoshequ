package com.hospital.escort.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {

    @NotBlank(message = "登录凭证code不能为空")
    private String code;

    private String nickname;

    private String avatar;
}