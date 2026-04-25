package com.hospital.escort.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.escort.common.Result;
import com.hospital.escort.entity.EscortApply;
import com.hospital.escort.service.EscortApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 陪诊员申请Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/escort-apply")
@RequiredArgsConstructor
public class EscortApplyController {

    private final EscortApplyService escortApplyService;

    /**
     * 提交申请
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody EscortApply apply) {
        log.info("提交陪诊员申请：{}", apply.getPhone());
        escortApplyService.submitApply(apply);
        return Result.success();
    }

    /**
     * 查询申请状态
     */
    @GetMapping("/status")
    public Result<EscortApply> getStatus(@RequestParam String phone) {
        log.info("查询申请状态：{}", phone);
        EscortApply apply = escortApplyService.getApplyByPhone(phone);
        return Result.success(apply);
    }

    /**
     * 管理员审核通过
     */
    @PostMapping("/approve/{id}")
    public Result<Void> approve(@PathVariable Long id) {
        log.info("审核通过申请：{}", id);
        escortApplyService.approve(id);
        return Result.success();
    }

    /**
     * 管理员审核拒绝
     */
    @PostMapping("/reject/{id}")
    public Result<Void> reject(@PathVariable Long id,
                               @RequestBody Map<String, String> params) {
        String reason = params.get("reason");
        log.info("审核拒绝申请：{}，原因：{}", id, reason);
        escortApplyService.reject(id, reason);
        return Result.success();
    }

    /**
     * 获取申请列表（管理员用）
     */
    @GetMapping("/list")
    public Result<IPage<EscortApply>> getList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        log.info("获取申请列表，状态：{}", status);

        Page<EscortApply> page = new Page<>(current, size);

        IPage<EscortApply> result = escortApplyService.lambdaQuery()
                .eq(status != null, EscortApply::getStatus, status)
                .orderByDesc(EscortApply::getCreateTime)
                .page(page);

        return Result.success(result);
    }
}