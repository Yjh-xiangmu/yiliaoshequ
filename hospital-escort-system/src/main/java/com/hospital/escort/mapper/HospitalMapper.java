package com.hospital.escort.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.escort.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医院Mapper接口
 */
@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
}