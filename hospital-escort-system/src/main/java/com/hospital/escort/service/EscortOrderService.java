package com.hospital.escort.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Constants;
import com.hospital.escort.common.PageResult;
import com.hospital.escort.dto.OrderCreateDTO;
import com.hospital.escort.entity.*;
import com.hospital.escort.mapper.EscortOrderMapper;
import com.hospital.escort.utils.OrderNoUtil;
import com.hospital.escort.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单Service
 */
@Slf4j
@Service
public class EscortOrderService extends ServiceImpl<EscortOrderMapper, EscortOrder> {

    private final UserService userService;
    private final HospitalService hospitalService;
    private final EscortService escortService;
    private final OrderEvaluationService orderEvaluationService;
    private final OrderPaymentService orderPaymentService;

    // 构造函数注入，对 OrderPaymentService 使用 @Lazy 打破循环依赖
    public EscortOrderService(
            UserService userService,
            HospitalService hospitalService,
            EscortService escortService,
            OrderEvaluationService orderEvaluationService,
            @Lazy OrderPaymentService orderPaymentService) {
        this.userService = userService;
        this.hospitalService = hospitalService;
        this.escortService = escortService;
        this.orderEvaluationService = orderEvaluationService;
        this.orderPaymentService = orderPaymentService;
    }

    /**
     * 创建订单
     */
    public OrderVO createOrder(Long userId, OrderCreateDTO dto) {
        // 1. 验证用户
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证医院
        Hospital hospital = hospitalService.getById(dto.getHospitalId());
        if (hospital == null) {
            throw new BusinessException("医院不存在");
        }

        // 3. 创建订单
        EscortOrder order = new EscortOrder();
        order.setUserId(userId);
        order.setOrderNo(OrderNoUtil.generate());
        order.setHospitalId(dto.getHospitalId());
        order.setHospitalName(dto.getHospitalName());
        order.setDepartment(dto.getDepartment());
        order.setAppointmentTime(dto.getAppointmentTime());
        order.setPatientName(dto.getPatientName());
        order.setPatientPhone(dto.getPatientPhone());
        order.setPatientIdCard(dto.getPatientIdCard());
        order.setServiceType(dto.getServiceType());
        order.setSpecialRequirements(dto.getSpecialRequirements());
        order.setPrice(new BigDecimal("100.00")); // 暂时固定价格
        order.setStatus(Constants.OrderStatus.PENDING);

        save(order);

        // 4. 转换VO返回
        return convertToVO(order);
    }

    /**
     * 取消订单
     */
    public void cancelOrder(Long orderId, Long userId, String reason) {
        EscortOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证是否是订单创建者
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 只有待接单状态才能取消
        if (order.getStatus() != Constants.OrderStatus.PENDING) {
            throw new BusinessException("当前状态不能取消");
        }

        order.setStatus(Constants.OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        updateById(order);
    }

    /**
     * 获取我的订单列表
     */
    public PageResult<OrderVO> getMyOrders(Long userId, Integer status, Integer current, Integer size) {
        Page<EscortOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<EscortOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EscortOrder::getUserId, userId);

        if (status != null) {
            wrapper.eq(EscortOrder::getStatus, status);
        }

        wrapper.orderByDesc(EscortOrder::getCreateTime);

        IPage<EscortOrder> orderPage = page(page, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<OrderVO> result = new PageResult<>();
        result.setTotal(orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 获取订单详情
     */
    public OrderVO getOrderDetail(Long orderId) {
        EscortOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    /**
     * 获取待接订单列表（陪诊员使用）
     */
    public PageResult<OrderVO> getPendingOrders(Integer current, Integer size) {
        Page<EscortOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<EscortOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EscortOrder::getStatus, Constants.OrderStatus.PENDING);
        wrapper.orderByDesc(EscortOrder::getCreateTime);

        IPage<EscortOrder> orderPage = page(page, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<OrderVO> result = new PageResult<>();
        result.setTotal(orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 陪诊员接单
     */
    public void acceptOrder(Long orderId, Long escortId) {
        EscortOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Constants.OrderStatus.PENDING) {
            throw new BusinessException("订单状态不正确");
        }

        // 设置陪诊员
        order.setEscortId(escortId);
        order.setStatus(Constants.OrderStatus.ACCEPTED);
        updateById(order);

        // 增加陪诊员接单数
        escortService.incrementOrderCount(escortId);
    }

    /**
     * 获取陪诊员的订单列表
     */
    public PageResult<OrderVO> getEscortOrders(Long escortId, Integer status, Integer current, Integer size) {
        Page<EscortOrder> page = new Page<>(current, size);

        LambdaQueryWrapper<EscortOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EscortOrder::getEscortId, escortId);

        if (status != null) {
            wrapper.eq(EscortOrder::getStatus, status);
        }

        wrapper.orderByDesc(EscortOrder::getCreateTime);

        IPage<EscortOrder> orderPage = page(page, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<OrderVO> result = new PageResult<>();
        result.setTotal(orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(EscortOrder order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 设置状态文本
        vo.setStatusText(getStatusText(order.getStatus()));

        // 如果有陪诊员，填充陪诊员信息
        if (order.getEscortId() != null) {
            Escort escort = escortService.getById(order.getEscortId());
            if (escort != null) {
                vo.setEscortName(escort.getName());
                vo.setEscortPhone(escort.getPhone());
            }
        }

        // 检查是否已评价
        if (order.getStatus() == Constants.OrderStatus.COMPLETED) {
            OrderEvaluation evaluation = orderEvaluationService.lambdaQuery()
                    .eq(OrderEvaluation::getOrderId, order.getId())
                    .one();
            vo.setHasEvaluation(evaluation != null);

            // 检查支付状态
            OrderPayment payment = orderPaymentService.lambdaQuery()
                    .eq(OrderPayment::getOrderId, order.getId())
                    .eq(OrderPayment::getStatus, 1)
                    .one();
            vo.setIsPaid(payment != null);
        } else {
            vo.setHasEvaluation(false);
            vo.setIsPaid(false);
        }

        return vo;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";

        switch (status) {
            case Constants.OrderStatus.PENDING:
                return "待接单";
            case Constants.OrderStatus.ACCEPTED:
                return "已接单";
            case Constants.OrderStatus.IN_SERVICE:
                return "服务中";
            case Constants.OrderStatus.COMPLETED:
                return "已完成";
            case Constants.OrderStatus.CANCELLED:
                return "已取消";
            default:
                return "未知";
        }
    }
}