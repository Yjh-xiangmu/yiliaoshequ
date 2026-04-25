package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Constants;
import com.hospital.escort.dto.EscortLoginDTO;
import com.hospital.escort.entity.Escort;
import com.hospital.escort.mapper.EscortMapper;
import com.hospital.escort.utils.JwtUtil;
import com.hospital.escort.utils.PasswordUtil;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 陪诊员Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EscortService extends ServiceImpl<EscortMapper, Escort> {

    private final JwtUtil jwtUtil;

    /**
     * 陪诊员登录
     */
    public UserLoginVO login(EscortLoginDTO dto) {
        // 1. 查询陪诊员
        Escort escort = lambdaQuery()
                .eq(Escort::getPhone, dto.getPhone())
                .one();

        if (escort == null) {
            throw new BusinessException("手机号或密码错误");
        }

        // 2. 验证密码
        if (escort.getPassword() == null || !PasswordUtil.matches(dto.getPassword(), escort.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }

        // 3. 检查状态
        if (escort.getStatus() != Constants.EscortStatus.NORMAL) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 4. 生成Token
        String token = jwtUtil.generateToken(escort.getId(), Constants.UserType.ESCORT);

        // 5. 封装返回结果
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(escort.getId());
        vo.setToken(token);
        vo.setNickname(escort.getName());
        vo.setAvatar(escort.getAvatar());
        vo.setPhone(escort.getPhone());
        vo.setIsNewUser(false);

        return vo;
    }

    /**
     * 增加接单数量
     */
    public void incrementOrderCount(Long escortId) {
        Escort escort = getById(escortId);
        if (escort != null) {
            escort.setOrderCount(escort.getOrderCount() + 1);
            updateById(escort);
        }
    }
}