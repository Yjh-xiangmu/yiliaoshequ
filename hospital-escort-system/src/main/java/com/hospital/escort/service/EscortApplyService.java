package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.entity.Escort;
import com.hospital.escort.entity.EscortApply;
import com.hospital.escort.mapper.EscortApplyMapper;
import com.hospital.escort.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 陪诊员申请Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EscortApplyService extends ServiceImpl<EscortApplyMapper, EscortApply> {

    private final EscortService escortService;

    /**
     * 提交申请
     */
    public void submitApply(EscortApply apply) {
        // 检查手机号是否已注册
        Escort existing = escortService.lambdaQuery()
                .eq(Escort::getPhone, apply.getPhone())
                .one();
        if (existing != null) {
            throw new BusinessException("该手机号已注册为陪诊员");
        }

        // 检查是否已有待审核申请
        EscortApply pendingApply = lambdaQuery()
                .eq(EscortApply::getPhone, apply.getPhone())
                .eq(EscortApply::getStatus, 0)
                .one();
        if (pendingApply != null) {
            throw new BusinessException("您已提交申请，请等待审核");
        }

        // 加密密码
        apply.setPassword(PasswordUtil.encode(apply.getPassword()));
        apply.setStatus(0); // 待审核
        save(apply);
    }

    /**
     * 审核通过 - 创建陪诊员账号
     */
    public void approve(Long applyId) {
        EscortApply apply = getById(applyId);
        if (apply == null) {
            throw new BusinessException("申请不存在");
        }

        // 更新申请状态
        apply.setStatus(1);
        apply.setAuditTime(LocalDateTime.now());
        updateById(apply);

        // 创建陪诊员账号
        Escort escort = new Escort();
        escort.setName(apply.getRealName());
        escort.setPhone(apply.getPhone());
        escort.setPassword(apply.getPassword());
        escort.setIdCard(apply.getIdCardNo());
        escort.setStatus(1); // 正常状态
        escortService.save(escort);

        log.info("陪诊员申请审核通过，创建账号：{}", apply.getPhone());
    }

    /**
     * 审核拒绝
     */
    public void reject(Long applyId, String reason) {
        EscortApply apply = getById(applyId);
        if (apply == null) {
            throw new BusinessException("申请不存在");
        }

        apply.setStatus(2);
        apply.setRejectReason(reason);
        apply.setAuditTime(LocalDateTime.now());
        updateById(apply);
    }

    /**
     * 查询申请状态
     */
    public EscortApply getApplyByPhone(String phone) {
        return lambdaQuery()
                .eq(EscortApply::getPhone, phone)
                .orderByDesc(EscortApply::getCreateTime)
                .last("LIMIT 1")
                .one();
    }
}