package com.hospital.escort.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.escort.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息通知Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}