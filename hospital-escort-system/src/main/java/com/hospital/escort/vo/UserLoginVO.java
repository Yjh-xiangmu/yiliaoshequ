package com.hospital.escort.vo;

import lombok.Data;

/**
 * 用户登录返回VO
 */
@Data
public class UserLoginVO {

    /** 用户ID */
    private Long userId;

    /** token */
    private String token;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 是否新用户 */
    private Boolean isNewUser;

    /** 用户类型：user/escort */
    private String userType;
}