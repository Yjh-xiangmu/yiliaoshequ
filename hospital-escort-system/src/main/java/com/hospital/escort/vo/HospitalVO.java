package com.hospital.escort.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 医院信息VO
 */
@Data
public class HospitalVO {

    private Long id;

    private String name;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    private String level;

    private List<String> departments;
}