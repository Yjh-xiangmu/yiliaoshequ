package com.hospital.escort.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 陪诊员实体类
 */
@Data
@TableName("escort")
public class Escort implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String openid;  // 可选，陪诊员可以不绑定微信

    private String name;

    private String phone;

    private String password;  // 添加这一行

    private String avatar;

    private Integer gender;

    private Integer age;

    private String idCard;

    private String healthCert;

    private String escortCert;

    private String introduction;

    private String serviceAreas;

    private BigDecimal rating;

    private Integer orderCount;

    private Integer status;
    /** 累计收入 */
    private BigDecimal totalIncome;

    private Integer auditStatus;

    private String auditRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}