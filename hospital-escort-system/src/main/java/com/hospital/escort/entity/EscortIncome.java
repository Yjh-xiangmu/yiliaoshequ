package com.hospital.escort.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 陪诊员收入实体类
 */
@Data
@TableName("escort_income")
public class EscortIncome implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long escortId;
    private Long orderId;
    private String orderNo;
    private BigDecimal amount;

    /** 结算状态：0-待结算，1-已结算 */
    private Integer status;

    private LocalDateTime settleTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}