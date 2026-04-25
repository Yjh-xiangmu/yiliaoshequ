package com.hospital.escort.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 陪诊员注册申请实体类
 */
@Data
@TableName("escort_apply")
public class EscortApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String realName;
    private String phone;
    private String idCardNo;
    private String idCardFront;
    private String idCardBack;
    private String healthCert;
    private String escortCert;
    private String password;
    private String intro;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;
    private String rejectReason;
    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}