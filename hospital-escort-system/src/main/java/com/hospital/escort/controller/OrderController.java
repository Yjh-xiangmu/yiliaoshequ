package com.hospital.escort.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Constants;
import com.hospital.escort.common.PageResult;
import com.hospital.escort.common.Result;
import com.hospital.escort.dto.OrderCreateDTO;
import com.hospital.escort.entity.EscortOrder;
import com.hospital.escort.service.EscortOrderService;
import com.hospital.escort.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final EscortOrderService escortOrderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody OrderCreateDTO dto,
                                       @RequestAttribute Long userId) {
        log.info("创建订单，用户ID：{}，订单信息：{}", userId, dto);
        OrderVO orderVO = escortOrderService.createOrder(userId, dto);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId,
                                    @RequestBody Map<String, String> params,
                                    @RequestAttribute Long userId) {
        String reason = params.get("reason");
        log.info("取消订单，订单ID：{}，原因：{}", orderId, reason);
        escortOrderService.cancelOrder(orderId, userId, reason);
        return Result.success();
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my-orders")
    public Result<PageResult<OrderVO>> getMyOrders(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestAttribute Long userId) {
        log.info("获取我的订单列表，用户ID：{}，状态：{}，页码：{}", userId, status, current);
        PageResult<OrderVO> pageResult = escortOrderService.getMyOrders(userId, status, current, size);
        return Result.success(pageResult);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        log.info("获取订单详情，订单ID：{}", orderId);
        OrderVO orderVO = escortOrderService.getOrderDetail(orderId);
        return Result.success(orderVO);
    }

    /**
     * 更新订单状态（陪诊员使用）
     */
    @PostMapping("/update-status/{orderId}")
    public Result<Void> updateStatus(@PathVariable Long orderId,
                                     @RequestBody Map<String, Integer> params,
                                     @RequestAttribute Long userId,
                                     @RequestAttribute String userType) {
        Integer status = params.get("status");
        if (status == null) {
            throw new BusinessException("状态参数不能为空");
        }

        // 只有陪诊员可以更新订单状态
        if (!Constants.UserType.ESCORT.equals(userType)) {
            throw new BusinessException("无权限操作");
        }

        log.info("更新订单状态，订单ID：{}，新状态：{}", orderId, status);

        EscortOrder order = escortOrderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证是否是该陪诊员的订单
        if (!order.getEscortId().equals(userId)) {
            throw new BusinessException("这不是您的订单");
        }

        // 验证状态流转合法性
        if (order.getStatus() == Constants.OrderStatus.ACCEPTED && status == Constants.OrderStatus.IN_SERVICE) {
            // 已接单 -> 服务中
            order.setStatus(Constants.OrderStatus.IN_SERVICE);
        } else if (order.getStatus() == Constants.OrderStatus.IN_SERVICE && status == Constants.OrderStatus.COMPLETED) {
            // 服务中 -> 已完成
            order.setStatus(Constants.OrderStatus.COMPLETED);
        } else {
            throw new BusinessException("订单状态流转不合法");
        }

        escortOrderService.updateById(order);
        return Result.success();
    }
}