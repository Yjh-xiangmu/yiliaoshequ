package com.hospital.escort.controller;

import com.hospital.escort.common.Constants;
import com.hospital.escort.common.Result;
import com.hospital.escort.entity.EscortIncome;
import com.hospital.escort.service.EscortIncomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 陪诊员收入Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/escort-income")
@RequiredArgsConstructor
public class EscortIncomeController {

    private final EscortIncomeService escortIncomeService;

    /**
     * 获取收入列表
     */
    @GetMapping("/list")
    public Result<List<EscortIncome>> getIncomeList(@RequestAttribute Long userId,
                                                    @RequestAttribute String userType) {
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限");
        }

        log.info("获取陪诊员收入列表，陪诊员ID：{}", userId);
        List<EscortIncome> list = escortIncomeService.getIncomeList(userId);
        return Result.success(list);
    }

    /**
     * 获取累计收入
     */
    @GetMapping("/total")
    public Result<BigDecimal> getTotalIncome(@RequestAttribute Long userId,
                                             @RequestAttribute String userType) {
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限");
        }

        log.info("获取陪诊员累计收入，陪诊员ID：{}", userId);
        BigDecimal total = escortIncomeService.getTotalIncome(userId);
        return Result.success(total);
    }
}