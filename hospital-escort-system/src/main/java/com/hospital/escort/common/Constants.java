package com.hospital.escort.common;

/**
 * 系统常量类
 */
public class Constants {

    /**
     * 用户状态
     */
    public static class UserStatus {
        public static final int DISABLED = 0;  // 禁用
        public static final int NORMAL = 1;     // 正常
    }

    /**
     * 陪诊员状态
     */
    public static class EscortStatus {
        public static final int PENDING_AUDIT = 0;  // 待审核
        public static final int NORMAL = 1;         // 正常
        public static final int DISABLED = 2;       // 已禁用
    }

    /**
     * 陪诊员审核状态
     */
    public static class EscortAuditStatus {
        public static final int PENDING = 0;    // 待审核
        public static final int APPROVED = 1;   // 已通过
        public static final int REJECTED = 2;   // 未通过
    }

    /**
     * 订单状态
     */
    public static class OrderStatus {
        public static final int PENDING = 0;      // 待接单
        public static final int ACCEPTED = 1;     // 已接单
        public static final int IN_SERVICE = 2;   // 服务中
        public static final int COMPLETED = 3;    // 已完成
        public static final int CANCELLED = 4;    // 已取消
    }

    /**
     * 通知类型
     */
    public static class NotificationType {
        public static final int ORDER = 1;    // 订单通知
        public static final int SYSTEM = 2;   // 系统通知
        public static final int AUDIT = 3;    // 审核通知
    }

    /**
     * 性别
     */
    public static class Gender {
        public static final int UNKNOWN = 0;  // 未知
        public static final int MALE = 1;     // 男
        public static final int FEMALE = 2;   // 女
    }

    /**
     * 管理员角色
     */
    public static class AdminRole {
        public static final int SUPER_ADMIN = 1;    // 超级管理员
        public static final int NORMAL_ADMIN = 2;   // 普通管理员
    }

    /**
     * JWT相关
     */
    public static class JWT {
        public static final String TOKEN_HEADER = "Authorization";
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String USER_ID_CLAIM = "userId";
        public static final String USER_TYPE_CLAIM = "userType";
    }

    /**
     * 用户类型
     */
    public static class UserType {
        public static final String USER = "user";        // 普通用户
        public static final String ESCORT = "escort";    // 陪诊员
        public static final String ADMIN = "admin";      // 管理员
    }
}