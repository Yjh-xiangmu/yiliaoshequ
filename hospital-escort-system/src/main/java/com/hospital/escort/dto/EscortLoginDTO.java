package com.hospital.escort.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 陪诊员登录DTO
 */
@Data
public class EscortLoginDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;
}