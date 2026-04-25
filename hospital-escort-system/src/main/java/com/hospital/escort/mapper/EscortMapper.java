package com.hospital.escort.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.escort.entity.Escort;
import org.apache.ibatis.annotations.Mapper;

/**
 * 陪诊员Mapper接口
 */
@Mapper
public interface EscortMapper extends BaseMapper<Escort> {
}