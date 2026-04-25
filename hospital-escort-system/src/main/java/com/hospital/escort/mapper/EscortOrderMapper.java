package com.hospital.escort.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.escort.entity.EscortOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 */
@Mapper
public interface EscortOrderMapper extends BaseMapper<EscortOrder> {
}