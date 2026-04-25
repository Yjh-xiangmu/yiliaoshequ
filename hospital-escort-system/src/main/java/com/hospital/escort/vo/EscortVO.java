package com.hospital.escort.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 陪诊员信息VO
 */
@Data
public class EscortVO {

    private Long id;

    private String name;

    private String avatar;

    private Integer gender;

    private Integer age;

    private String introduction;

    private BigDecimal rating;

    private Integer orderCount;

    private Integer status;
}