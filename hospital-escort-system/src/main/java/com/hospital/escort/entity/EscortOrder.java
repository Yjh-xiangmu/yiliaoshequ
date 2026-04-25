package com.hospital.escort.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 陪诊订单实体类
 */
@Data
@TableName("escort_order")
public class EscortOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long escortId;

    private Long hospitalId;

    private String hospitalName;

    private String department;

    private LocalDateTime appointmentTime;

    private String patientName;

    private String patientPhone;

    private String patientIdCard;

    private String serviceType;

    private String specialRequirements;

    private BigDecimal price;

    private Integer status;

    private String cancelReason;

    private LocalDateTime acceptTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private LocalDateTime cancelTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
    @TableField(exist = false)
    private String escortName;
}