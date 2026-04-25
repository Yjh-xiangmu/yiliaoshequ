package com.hospital.escort.controller;

import com.hospital.escort.common.Constants;
import com.hospital.escort.common.PageResult;
import com.hospital.escort.common.Result;
import com.hospital.escort.dto.EscortLoginDTO;
import com.hospital.escort.entity.EscortOrder;
import com.hospital.escort.service.EscortIncomeService;
import com.hospital.escort.service.EscortOrderService;
import com.hospital.escort.service.EscortService;
import com.hospital.escort.vo.OrderVO;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 陪诊员Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/escort")
@RequiredArgsConstructor
public class EscortController {

    private final EscortService escortService;
    private final EscortOrderService escortOrderService;
    private final EscortIncomeService escortIncomeService;

    /**
     * 陪诊员登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody EscortLoginDTO dto) {
        log.info("陪诊员登录，手机号：{}", dto.getPhone());
        UserLoginVO vo = escortService.login(dto);
        return Result.success(vo);
    }

    /**
     * 获取陪诊员信息
     */
    @GetMapping("/info")
    public Result<UserLoginVO> getInfo(@RequestAttribute Long userId) {
        log.info("获取陪诊员信息，ID：{}", userId);
        // TODO: 实现获取陪诊员详细信息
        return Result.success(new UserLoginVO());
    }

    /**
     * 获取待接订单列表
     */
    @GetMapping("/pending-orders")
    public Result<PageResult<OrderVO>> getPendingOrders(@RequestParam(defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取待接订单列表，页码：{}", current);
        PageResult<OrderVO> pageResult = escortOrderService.getPendingOrders(current, size);
        return Result.success(pageResult);
    }

    /**
     * 接单
     */
    @PostMapping("/accept/{orderId}")
    public Result<Void> acceptOrder(@PathVariable Long orderId,
                                    @RequestAttribute Long userId,
                                    @RequestAttribute String userType) {
        // 验证是否是陪诊员
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限操作");
        }

        log.info("陪诊员接单，陪诊员ID：{}，订单ID：{}", userId, orderId);
        escortOrderService.acceptOrder(orderId, userId);
        return Result.success();
    }

    /**
     * 获取陪诊员的订单列表
     */
    @GetMapping("/my-orders")
    public Result<PageResult<OrderVO>> getMyOrders(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestAttribute Long userId,
                                                   @RequestAttribute String userType) {
        // 验证是否是陪诊员
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限操作");
        }

        log.info("获取陪诊员订单列表，陪诊员ID：{}，状态：{}", userId, status);
        PageResult<OrderVO> pageResult = escortOrderService.getEscortOrders(userId, status, current, size);
        return Result.success(pageResult);
    }

    /**
     * 获取陪诊员统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestAttribute Long userId,
                                                     @RequestAttribute String userType) {
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限");
        }

        log.info("获取陪诊员统计数据，陪诊员ID：{}", userId);

        Map<String, Object> stats = new HashMap<>();

        // 订单统计
        long acceptedCount = escortOrderService.lambdaQuery()
                .eq(EscortOrder::getEscortId, userId)
                .eq(EscortOrder::getStatus, Constants.OrderStatus.ACCEPTED)
                .count();

        long inServiceCount = escortOrderService.lambdaQuery()
                .eq(EscortOrder::getEscortId, userId)
                .eq(EscortOrder::getStatus, Constants.OrderStatus.IN_SERVICE)
                .count();

        long completedCount = escortOrderService.lambdaQuery()
                .eq(EscortOrder::getEscortId, userId)
                .eq(EscortOrder::getStatus, Constants.OrderStatus.COMPLETED)
                .count();

        stats.put("acceptedCount", acceptedCount);
        stats.put("inServiceCount", inServiceCount);
        stats.put("completedCount", completedCount);

        // 今日收入（今天创建的收入记录）
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<com.hospital.escort.entity.EscortIncome> todayIncomes = escortIncomeService.lambdaQuery()
                .eq(com.hospital.escort.entity.EscortIncome::getEscortId, userId)
                .ge(com.hospital.escort.entity.EscortIncome::getCreateTime, startOfDay)
                .lt(com.hospital.escort.entity.EscortIncome::getCreateTime, endOfDay)
                .list();

        BigDecimal todayIncome = todayIncomes.stream()
                .map(com.hospital.escort.entity.EscortIncome::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("todayIncome", todayIncome);
        stats.put("todayOrderCount", todayIncomes.size());

        return Result.success(stats);
    }
}