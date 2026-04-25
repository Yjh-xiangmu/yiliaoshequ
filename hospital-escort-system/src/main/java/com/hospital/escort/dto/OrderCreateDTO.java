package com.hospital.escort.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "医院ID不能为空")
    private Long hospitalId;

    @NotBlank(message = "医院名称不能为空")
    private String hospitalName;

    @NotBlank(message = "就诊科室不能为空")
    private String department;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    @NotBlank(message = "就诊人姓名不能为空")
    private String patientName;

    @NotBlank(message = "就诊人电话不能为空")
    private String patientPhone;

    private String patientIdCard;

    private String serviceType;

    private String specialRequirements;
}