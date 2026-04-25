package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.entity.EscortOrder;
import com.hospital.escort.entity.OrderPayment;
import com.hospital.escort.mapper.OrderPaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单支付Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentService extends ServiceImpl<OrderPaymentMapper, OrderPayment> {

    private final EscortOrderService escortOrderService;
    private final EscortIncomeService escortIncomeService;

    /**
     * 创建支付订单
     */
    public OrderPayment createPayment(Long orderId, Long userId) {
        EscortOrder order = escortOrderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查是否已有支付记录
        OrderPayment existing = lambdaQuery()
                .eq(OrderPayment::getOrderId, orderId)
                .eq(OrderPayment::getStatus, 1)
                .one();
        if (existing != null) {
            throw new BusinessException("订单已支付");
        }

        // 创建支付记录
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(order.getPrice());
        payment.setPaymentMethod("MOCK");
        payment.setStatus(0); // 待支付
        save(payment);

        return payment;
    }

    /**
     * 模拟支付
     */
    @Transactional
    public OrderPayment mockPay(Long paymentId, Long userId) {
        OrderPayment payment = getById(paymentId);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        if (payment.getStatus() == 1) {
            throw new BusinessException("订单已支付");
        }

        // 生成支付流水号
        String paymentNo = generatePaymentNo();

        // 更新支付状态
        payment.setStatus(1);
        payment.setPaymentNo(paymentNo);
        payment.setPayTime(LocalDateTime.now());
        updateById(payment);

        // 获取订单信息
        EscortOrder order = escortOrderService.getById(payment.getOrderId());
        if (order != null && order.getEscortId() != null) {
            // 创建陪诊员收入记录
            escortIncomeService.createIncome(
                    order.getEscortId(),
                    order.getId(),
                    order.getOrderNo(),
                    payment.getAmount()
            );
        }

        log.info("模拟支付成功，支付流水号：{}", paymentNo);
        return payment;
    }

    /**
     * 根据订单ID获取支付记录
     */
    public OrderPayment getByOrderId(Long orderId) {
        return lambdaQuery()
                .eq(OrderPayment::getOrderId, orderId)
                .orderByDesc(OrderPayment::getCreateTime)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9000) + 1000;
        return "PAY" + timestamp + random;
    }
}