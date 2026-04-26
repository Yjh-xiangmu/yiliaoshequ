package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.Constants;
import com.hospital.escort.dto.UserLoginDTO;
import com.hospital.escort.entity.User;
import com.hospital.escort.mapper.UserMapper;
import com.hospital.escort.utils.JwtUtil;
import com.hospital.escort.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * 用户Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 用户登录
     */
    public UserLoginVO login(UserLoginDTO dto) {
        String openid;

        try {
            // 调用微信接口获取openid
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session" +
                            "?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appid, secret, dto.getCode()
            );

            log.info("调用微信接口，URL：{}", url);

            // 用 String 接收，避免 content-type 不匹配问题
            RestTemplate stringRestTemplate = new RestTemplate();
            stringRestTemplate.getMessageConverters().add(
                    0, new StringHttpMessageConverter(StandardCharsets.UTF_8)
            );

            String responseStr = stringRestTemplate.getForObject(url, String.class);
            log.info("微信接口返回原始数据：{}", responseStr);

            // 解析JSON字符串
            Map<String, Object> result = objectMapper.readValue(
                    responseStr,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            );

            log.info("微信接口解析结果：{}", result);

            if (result.containsKey("errcode")) {
                int errcode = Integer.parseInt(result.get("errcode").toString());
                if (errcode != 0) {
                    throw new BusinessException("微信登录失败：" + result.get("errmsg"));
                }
            }

            openid = (String) result.get("openid");
            if (openid == null || openid.isEmpty()) {
                throw new BusinessException("获取openid失败");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常：", e);
            throw new BusinessException("微信登录失败，请重试");
        }

        return buildLoginVO(openid, dto.getNickname(), dto.getAvatar());
    }

    /**
     * 构建登录VO
     */
    public UserLoginVO buildLoginVO(String openid, String nickname, String avatar) {
        // 查找用户
        User user = lambdaQuery()
                .eq(User::getOpenid, openid)
                .one();

        if (user == null) {
            // 新用户，创建账号
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname != null ? nickname : "微信用户");
            user.setAvatar(avatar);
            save(user);
            log.info("新用户注册，openid：{}", openid);
        } else {
            if (user.getStatus() == Constants.UserStatus.DISABLED) {
                throw new BusinessException("账号已被封禁，请联系管理员");
            }
            // 老用户：只更新头像，不覆盖用户自己设置的昵称
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar);
                updateById(user);
            }
            log.info("老用户登录，userId：{}，nickname：{}", user.getId(), user.getNickname());
        }

        // 生成token
        String token = JwtUtil.generateToken(user.getId(), Constants.UserType.USER);

        // 构建返回VO，使用数据库中保存的昵称
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setToken(token);
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setIsNewUser(false);

        return vo;
    }
}