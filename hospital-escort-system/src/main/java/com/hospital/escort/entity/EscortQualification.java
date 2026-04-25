package com.hospital.escort.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 陪诊员资质实体类
 */
@Data
@TableName("escort_qualification")
public class EscortQualification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long escortId;
    private String idCardNo;
    private String idCardFront;
    private String idCardBack;
    private String healthCert;
    private String escortCert;
    private String realName;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;
    private String rejectReason;
    private LocalDateTime submitTime;
    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}