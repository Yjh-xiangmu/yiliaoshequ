package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.entity.Escort;
import com.hospital.escort.entity.EscortIncome;
import com.hospital.escort.mapper.EscortIncomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 陪诊员收入Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EscortIncomeService extends ServiceImpl<EscortIncomeMapper, EscortIncome> {

    private final EscortService escortService;

    /**
     * 创建收入记录（订单支付成功后调用）
     */
    @Transactional
    public void createIncome(Long escortId, Long orderId, String orderNo, BigDecimal amount) {
        // 检查是否已存在
        EscortIncome existing = lambdaQuery()
                .eq(EscortIncome::getOrderId, orderId)
                .one();
        if (existing != null) {
            log.warn("订单{}已有收入记录，跳过创建", orderNo);
            return;
        }

        // 创建收入记录
        EscortIncome income = new EscortIncome();
        income.setEscortId(escortId);
        income.setOrderId(orderId);
        income.setOrderNo(orderNo);
        income.setAmount(amount);
        income.setStatus(0); // 待结算
        save(income);

        // 更新陪诊员累计收入
        Escort escort = escortService.getById(escortId);
        if (escort != null) {
            BigDecimal currentIncome = escort.getTotalIncome() != null ? escort.getTotalIncome() : BigDecimal.ZERO;
            escort.setTotalIncome(currentIncome.add(amount));
            escortService.updateById(escort);
        }

        log.info("创建收入记录成功，陪诊员ID：{}，订单号：{}，金额：{}", escortId, orderNo, amount);
    }

    /**
     * 获取陪诊员收入列表
     */
    public List<EscortIncome> getIncomeList(Long escortId) {
        return lambdaQuery()
                .eq(EscortIncome::getEscortId, escortId)
                .orderByDesc(EscortIncome::getCreateTime)
                .list();
    }

    /**
     * 获取陪诊员收入统计
     */
    public BigDecimal getTotalIncome(Long escortId) {
        List<EscortIncome> list = lambdaQuery()
                .eq(EscortIncome::getEscortId, escortId)
                .list();

        return list.stream()
                .map(EscortIncome::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 结算收入
     */
    @Transactional
    public void settleIncome(Long incomeId) {
        EscortIncome income = getById(incomeId);
        if (income != null && income.getStatus() == 0) {
            income.setStatus(1);
            income.setSettleTime(LocalDateTime.now());
            updateById(income);
        }
    }
}