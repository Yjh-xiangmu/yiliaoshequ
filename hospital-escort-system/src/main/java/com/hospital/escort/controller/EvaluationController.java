package com.hospital.escort.controller;

import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Constants;
import com.hospital.escort.common.Result;
import com.hospital.escort.entity.EscortOrder;
import com.hospital.escort.entity.OrderEvaluation;
import com.hospital.escort.service.EscortOrderService;
import com.hospital.escort.service.OrderEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
/**
 * 评价Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final OrderEvaluationService evaluationService;
    private final EscortOrderService escortOrderService;

    /**
     * 创建评价
     */
    @PostMapping("/create")
    public Result<Void> createEvaluation(@RequestBody Map<String, Object> params,
                                         @RequestAttribute Long userId) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Integer rating = Integer.valueOf(params.get("rating").toString());
        String tags = (String) params.get("tags");
        String comment = (String) params.get("comment");
        Integer anonymous = Integer.valueOf(params.get("anonymous").toString());

        log.info("创建评价，用户ID：{}，订单ID：{}", userId, orderId);

        // 验证订单
        EscortOrder order = escortOrderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证是否是订单创建者
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权评价此订单");
        }

        // 验证订单状态
        if (order.getStatus() != Constants.OrderStatus.COMPLETED) {
            throw new BusinessException("订单未完成，无法评价");
        }

        // 检查是否已评价
        OrderEvaluation existingEvaluation = evaluationService.lambdaQuery()
                .eq(OrderEvaluation::getOrderId, orderId)
                .one();
        if (existingEvaluation != null) {
            throw new BusinessException("订单已评价");
        }

        // 创建评价
        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setUserId(userId);
        evaluation.setEscortId(order.getEscortId());
        evaluation.setRating(rating);
        evaluation.setTags(tags);
        evaluation.setComment(comment);
        evaluation.setAnonymous(anonymous);

        evaluationService.save(evaluation);

        return Result.success();
    }

    /**
     * 获取订单评价
     */
    @GetMapping("/order/{orderId}")
    public Result<OrderEvaluation> getEvaluation(@PathVariable Long orderId) {
        log.info("获取订单评价，订单ID：{}", orderId);

        OrderEvaluation evaluation = evaluationService.lambdaQuery()
                .eq(OrderEvaluation::getOrderId, orderId)
                .one();

        return Result.success(evaluation);
    }
    /**
     * 获取我的评价列表
     */
    @GetMapping("/my-list")
    public Result<List<OrderEvaluation>> getMyEvaluations(@RequestAttribute Long userId) {
        log.info("获取我的评价列表，用户ID：{}", userId);

        List<OrderEvaluation> list = evaluationService.lambdaQuery()
                .eq(OrderEvaluation::getUserId, userId)
                .orderByDesc(OrderEvaluation::getCreateTime)
                .list();

        return Result.success(list);
    }
    /**
     * 获取陪诊员收到的评价列表
     */
    @GetMapping("/escort-list")
    public Result<List<Map<String, Object>>> getEscortEvaluations(@RequestAttribute Long userId,
                                                                  @RequestAttribute String userType) {
        if (!Constants.UserType.ESCORT.equals(userType)) {
            return Result.error("无权限");
        }

        log.info("获取陪诊员评价列表，陪诊员ID：{}", userId);

        List<OrderEvaluation> list = evaluationService.lambdaQuery()
                .eq(OrderEvaluation::getEscortId, userId)
                .orderByDesc(OrderEvaluation::getCreateTime)
                .list();

        // 填充订单号
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderEvaluation eval : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", eval.getId());
            map.put("rating", eval.getRating());
            map.put("tags", eval.getTags());
            map.put("comment", eval.getComment());
            map.put("anonymous", eval.getAnonymous());
            map.put("createTime", eval.getCreateTime());

            // 获取订单号
            EscortOrder order = escortOrderService.getById(eval.getOrderId());
            if (order != null) {
                map.put("orderNo", order.getOrderNo());
            }

            result.add(map);
        }

        return Result.success(result);
    }
}