package com.hospital.escort.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息VO
 */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long hospitalId;

    private String hospitalName;

    private String department;

    private LocalDateTime appointmentTime;

    private String patientName;

    private String patientPhone;

    private String serviceType;

    private String specialRequirements;

    private BigDecimal price;

    private Integer status;

    private String statusText;

    private Long escortId;

    private String escortName;

    private String escortPhone;

    private LocalDateTime createTime;

    private LocalDateTime acceptTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;
    private Boolean hasEvaluation;
    /** 是否已支付 */
    private Boolean isPaid;
}