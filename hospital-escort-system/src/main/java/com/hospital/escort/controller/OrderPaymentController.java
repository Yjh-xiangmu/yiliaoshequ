package com.hospital.escort.controller;

import com.hospital.escort.common.Result;
import com.hospital.escort.entity.OrderPayment;
import com.hospital.escort.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单支付Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create/{orderId}")
    public Result<OrderPayment> createPayment(@PathVariable Long orderId,
                                              @RequestAttribute Long userId) {
        log.info("创建支付订单，订单ID：{}，用户ID：{}", orderId, userId);
        OrderPayment payment = orderPaymentService.createPayment(orderId, userId);
        return Result.success(payment);
    }

    /**
     * 模拟支付
     */
    @PostMapping("/mock-pay/{paymentId}")
    public Result<OrderPayment> mockPay(@PathVariable Long paymentId,
                                        @RequestAttribute Long userId) {
        log.info("模拟支付，支付ID：{}，用户ID：{}", paymentId, userId);
        OrderPayment payment = orderPaymentService.mockPay(paymentId, userId);
        return Result.success(payment);
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{orderId}")
    public Result<OrderPayment> getPaymentStatus(@PathVariable Long orderId) {
        log.info("查询支付状态，订单ID：{}", orderId);
        OrderPayment payment = orderPaymentService.getByOrderId(orderId);
        return Result.success(payment);
    }
}