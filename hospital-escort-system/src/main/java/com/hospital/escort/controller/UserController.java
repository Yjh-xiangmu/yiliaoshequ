package com.hospital.escort.controller;

import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Result;
import com.hospital.escort.dto.UserLoginDTO;
import com.hospital.escort.entity.User;
import com.hospital.escort.service.UserService;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        log.info("用户登录，code：{}", dto.getCode());
        UserLoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getInfo(@RequestAttribute Long userId) {
        log.info("获取用户信息，用户ID：{}", userId);
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update-info")
    public Result<Void> updateInfo(@RequestBody Map<String, String> params,
                                   @RequestAttribute Long userId) {
        String nickname = params.get("nickname");
        String phone = params.get("phone");

        log.info("更新用户信息，用户ID：{}，nickname：{}，phone：{}", userId, nickname, phone);

        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (phone != null && !phone.isEmpty()) {
            user.setPhone(phone);
        }

        userService.updateById(user);
        return Result.success();
    }
}