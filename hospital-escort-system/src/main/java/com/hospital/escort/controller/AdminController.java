package com.hospital.escort.controller;
import com.hospital.escort.entity.User;
import com.hospital.escort.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.escort.common.Result;
import com.hospital.escort.entity.Escort;
import com.hospital.escort.entity.EscortApply;
import com.hospital.escort.entity.EscortOrder;
import com.hospital.escort.service.AdminService;
import com.hospital.escort.service.EscortApplyService;
import com.hospital.escort.service.EscortOrderService;
import com.hospital.escort.service.EscortService;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final EscortApplyService escortApplyService;
    private final EscortOrderService escortOrderService;
    private final EscortService escortService;
    private final UserService userService;
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        log.info("管理员登录，用户名：{}", username);
        UserLoginVO vo = adminService.login(username, password);
        return Result.success(vo);
    }

    /**
     * 获取陪诊员申请列表
     */
    @GetMapping("/escort-apply/list")
    public Result<IPage<EscortApply>> getApplyList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        log.info("获取陪诊员申请列表，状态：{}", status);

        Page<EscortApply> page = new Page<>(current, size);

        IPage<EscortApply> result = escortApplyService.lambdaQuery()
                .eq(status != null, EscortApply::getStatus, status)
                .orderByDesc(EscortApply::getCreateTime)
                .page(page);

        return Result.success(result);
    }

    /**
     * 审核通过
     */
    @PostMapping("/escort-apply/approve/{id}")
    public Result<Void> approveApply(@PathVariable Long id) {
        log.info("审核通过陪诊员申请，ID：{}", id);
        escortApplyService.approve(id);
        return Result.success();
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/escort-apply/reject/{id}")
    public Result<Void> rejectApply(@PathVariable Long id,
                                    @RequestBody Map<String, String> params) {
        String reason = params.get("reason");
        log.info("审核拒绝陪诊员申请，ID：{}，原因：{}", id, reason);
        escortApplyService.reject(id, reason);
        return Result.success();
    }

    /**
     * 获取所有订单列表（支持搜索，含陪诊员信息）
     */
    @GetMapping("/order/list")
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String patientName) {
        log.info("获取订单列表，状态：{}，订单号：{}，患者姓名：{}", status, orderNo, patientName);

        Page<EscortOrder> page = new Page<>(current, size);

        IPage<EscortOrder> orderPage = escortOrderService.lambdaQuery()
                .eq(status != null, EscortOrder::getStatus, status)
                .like(orderNo != null && !orderNo.isEmpty(), EscortOrder::getOrderNo, orderNo)
                .like(patientName != null && !patientName.isEmpty(), EscortOrder::getPatientName, patientName)
                .orderByDesc(EscortOrder::getCreateTime)
                .page(page);

        // 填充陪诊员姓名
        List<EscortOrder> records = orderPage.getRecords();
        for (EscortOrder order : records) {
            if (order.getEscortId() != null) {
                Escort escort = escortService.getById(order.getEscortId());
                if (escort != null) {
                    order.setEscortName(escort.getName());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", orderPage.getTotal());
        result.put("current", orderPage.getCurrent());
        result.put("size", orderPage.getSize());

        return Result.success(result);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        log.info("获取管理员统计数据");

        Map<String, Object> stats = new HashMap<>();

        // 待审核申请数
        long pendingApplyCount = escortApplyService.lambdaQuery()
                .eq(EscortApply::getStatus, 0)
                .count();

        // 订单统计
        long totalOrderCount = escortOrderService.count();
        long pendingOrderCount = escortOrderService.lambdaQuery()
                .eq(EscortOrder::getStatus, 0)
                .count();
        long completedOrderCount = escortOrderService.lambdaQuery()
                .eq(EscortOrder::getStatus, 3)
                .count();

        stats.put("pendingApplyCount", pendingApplyCount);
        stats.put("totalOrderCount", totalOrderCount);
        stats.put("pendingOrderCount", pendingOrderCount);
        stats.put("completedOrderCount", completedOrderCount);

        return Result.success(stats);
    }
    /**
     * 获取用户列表（普通用户）
     */
    @GetMapping("/user/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        log.info("获取用户列表，昵称：{}，手机号：{}，状态：{}", nickname, phone, status);

        Page<User> page = new Page<>(current, size);

        IPage<User> userPage = userService.lambdaQuery()
                .like(nickname != null && !nickname.isEmpty(), User::getNickname, nickname)
                .like(phone != null && !phone.isEmpty(), User::getPhone, phone)
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreateTime)
                .page(page);

        // 统计每个用户的订单数
        List<User> records = userPage.getRecords();
        for (User user : records) {
            long orderCount = escortOrderService.lambdaQuery()
                    .eq(EscortOrder::getUserId, user.getId())
                    .count();
            user.setOrderCount((int) orderCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", userPage.getTotal());
        result.put("current", userPage.getCurrent());
        result.put("size", userPage.getSize());

        return Result.success(result);
    }

    /**
     * 获取陪诊员列表
     */
    @GetMapping("/escort/list")
    public Result<Map<String, Object>> getEscortList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        log.info("获取陪诊员列表，姓名：{}，手机号：{}，状态：{}", name, phone, status);

        Page<Escort> page = new Page<>(current, size);

        IPage<Escort> escortPage = escortService.lambdaQuery()
                .like(name != null && !name.isEmpty(), Escort::getName, name)
                .like(phone != null && !phone.isEmpty(), Escort::getPhone, phone)
                .eq(status != null, Escort::getStatus, status)
                .orderByDesc(Escort::getCreateTime)
                .page(page);

        // 统计每个陪诊员的订单数和收入
        List<Escort> records = escortPage.getRecords();
        for (Escort escort : records) {
            long orderCount = escortOrderService.lambdaQuery()
                    .eq(EscortOrder::getEscortId, escort.getId())
                    .count();
            escort.setOrderCount((int) orderCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", escortPage.getTotal());
        result.put("current", escortPage.getCurrent());
        result.put("size", escortPage.getSize());

        return Result.success(result);
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PostMapping("/user/update-status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                         @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("更新用户状态，用户ID：{}，状态：{}", id, status);

        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setStatus(status);
        userService.updateById(user);

        return Result.success();
    }

    /**
     * 更新陪诊员状态（启用/禁用）
     */
    @PostMapping("/escort/update-status/{id}")
    public Result<Void> updateEscortStatus(@PathVariable Long id,
                                           @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("更新陪诊员状态，陪诊员ID：{}，状态：{}", id, status);

        Escort escort = escortService.getById(id);
        if (escort == null) {
            return Result.error("陪诊员不存在");
        }

        escort.setStatus(status);
        escortService.updateById(escort);

        return Result.success();
    }
}