package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.entity.Admin;
import com.hospital.escort.mapper.AdminMapper;
import com.hospital.escort.utils.JwtUtil;
import com.hospital.escort.utils.PasswordUtil;
import com.hospital.escort.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员Service
 */
@Slf4j
@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    /**
     * 管理员登录
     */
    public UserLoginVO login(String username, String password) {
        Admin admin = lambdaQuery()
                .eq(Admin::getUsername, username)
                .one();

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 验证密码（先简单对比明文，如果数据库存的是加密的就用 PasswordUtil.matches）
        if (!password.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成token
        String token = JwtUtil.generateToken(admin.getId(), "admin");

        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(admin.getId());
        vo.setToken(token);
        vo.setNickname(admin.getRealName() != null ? admin.getRealName() : admin.getUsername());
        vo.setUserType("admin");
        vo.setIsNewUser(false);

        return vo;
    }
}