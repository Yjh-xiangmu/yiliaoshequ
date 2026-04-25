package com.hospital.escort.config;

import com.hospital.escort.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/login",                  // 用户登录
                        "/api/escort/login",                // 陪诊员登录
                        "/api/admin/login",                 // 管理员登录
                        "/api/admin/statistics",            // 管理员统计
                        "/api/admin/escort-apply/**",       // 管理员申请审核（所有）
                        "/api/admin/order/**",              // 管理员订单管理（所有）
                        "/api/hospital/list",               // 医院列表（公开）
                        "/api/hospital/all",                // 所有医院（公开）
                        "/api/hospital/detail/**",          // 医院详情（公开）
                        "/api/upload/**",
                        "/api/test/**",                     // 测试接口
                        "/api/escort-apply/submit",         // 陪诊员申请提交
                        "/api/escort-apply/status",         // 陪诊员申请状态查询
                        "/error",
                        "/cert/**",                     // 放行证件图片访问
                        "/avatar/**"                    // 放行头像访问
                );
    }

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 上传文件访问路径
        registry.addResourceHandler("/cert/**")
                .addResourceLocations("file:uploads/cert/");

        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:uploads/avatar/");
    }

}