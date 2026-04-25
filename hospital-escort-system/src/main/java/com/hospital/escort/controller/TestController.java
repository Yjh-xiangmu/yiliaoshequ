package com.hospital.escort.controller;

import com.hospital.escort.common.Constants;
import com.hospital.escort.common.Result;
import com.hospital.escort.entity.User;
import com.hospital.escort.service.UserService;
import com.hospital.escort.utils.JwtUtil;
import com.hospital.escort.utils.PasswordUtil;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试Controller
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello, Hospital Escort System!");
        data.put("time", LocalDateTime.now());
        data.put("status", "running");
        return Result.success(data);
    }

    /**
     * 测试用户登录（仅用于开发测试）
     */
    @PostMapping("/user-login")
    public Result<UserLoginVO> testUserLogin() {
        // 查询是否已有测试用户
        User user = userService.lambdaQuery()
                .eq(User::getOpenid, "test_openid_001")
                .one();

        // 如果没有，创建一个
        if (user == null) {
            user = new User();
            user.setOpenid("test_openid_001");
            user.setNickname("测试用户");
            user.setPhone("13800138000");
            user.setStatus(Constants.UserStatus.NORMAL);
            userService.save(user);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), Constants.UserType.USER);

        // 封装返回结果
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setToken(token);
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setIsNewUser(false);

        return Result.success(vo);
    }

    /**
     * 密码加密工具（用于生成测试账号密码）
     */
    @GetMapping("/encode-password")
    public Result<String> encodePassword(@RequestParam String password) {
        String encoded = PasswordUtil.encode(password);
        return Result.success(encoded);
    }
}