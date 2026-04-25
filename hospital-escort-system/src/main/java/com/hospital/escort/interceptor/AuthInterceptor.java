package com.hospital.escort.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.escort.common.Result;
import com.hospital.escort.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response);
            return false;
        }

        // 检查token是否过期
        if (jwtUtil.isTokenExpired(token)) {
            writeUnauthorized(response);
            return false;
        }

        try {
            // 从token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String userType = jwtUtil.getUserTypeFromToken(token);

            // 设置到请求属性中
            request.setAttribute("userId", userId);
            request.setAttribute("userType", userType);

            return true;
        } catch (Exception e) {
            log.error("Token解析失败：{}", e.getMessage());
            writeUnauthorized(response);
            return false;
        }
    }

    /**
     * 返回未授权响应
     */
    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(401, "未登录或登录已过期");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}