package com.demo.dp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 基础安全配置：
 * - 放行注册/登录，其他接口需要 JWT 认证。
 * - 关闭 CSRF，使用无状态会话。
 * - 禁用 httpBasic，改用 JWT。
 * - 提供 BCryptPasswordEncoder。
 * 
 * <p>接口访问权限说明：
 * <ul>
 *   <li>公开接口（无需认证）：
 *     <ul>
 *       <li>/api/users/login - 用户登录</li>
 *       <li>/api/users/register - 用户注册</li>
 *       <li>GET /api/shops - 获取商家列表（查询、搜索、筛选）</li>
 *       <li>GET /api/shops/{id} - 获取商家详情</li>
 *       <li>GET /api/shops/{shopId}/reviews - 获取商家点评列表</li>
 *     </ul>
 *   </li>
 *   <li>需要认证的接口：
 *     <ul>
 *       <li>POST /api/shops - 创建商家（管理端）</li>
 *       <li>PUT /api/shops/{id} - 更新商家（管理端）</li>
 *       <li>DELETE /api/shops/{id} - 删除商家（管理端）</li>
 *       <li>POST /api/shops/{shopId}/reviews - 创建点评（用户）</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT认证过滤器，用于解析和验证JWT token。
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 构造函数，通过依赖注入获取JwtAuthFilter实例。
     * 
     * @param jwtAuthFilter JWT认证过滤器，由Spring容器自动注入
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * 配置Spring Security的安全过滤链。
     * 
     * <p>安全配置说明：
     * <ul>
     *   <li>禁用CSRF：因为使用JWT无状态认证，不需要CSRF保护</li>
     *   <li>无状态会话：使用STATELESS模式，不创建HTTP Session</li>
     *   <li>接口权限：配置哪些接口需要认证，哪些允许匿名访问</li>
     *   <li>JWT过滤器：在UsernamePasswordAuthenticationFilter之前添加JWT认证过滤器</li>
     * </ul>
     * 
     * @param http HttpSecurity对象，用于配置安全规则
     * @return 配置好的SecurityFilterChain
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护：因为使用JWT无状态认证，不需要CSRF token
            .csrf(csrf -> csrf.disable())
            
            // 配置会话管理：使用无状态模式，不创建HTTP Session
            // 这样每次请求都需要携带JWT token，适合RESTful API
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置接口访问权限
            .authorizeHttpRequests(auth -> auth
                // 用户认证相关接口：允许匿名访问
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                
                // 商家查询接口：允许匿名访问（公开数据）
                // GET /api/shops - 获取商家列表（支持分页、搜索、筛选）
                .requestMatchers(HttpMethod.GET, "/api/shops").permitAll()
                // GET /api/shops/{id} - 获取商家详情
                .requestMatchers(HttpMethod.GET, "/api/shops/*").permitAll()
                
                // 点评查询接口：允许匿名访问（公开数据）
                // GET /api/shops/{shopId}/reviews - 获取商家点评列表
                .requestMatchers(HttpMethod.GET, "/api/shops/*/reviews").permitAll()
                // GET /api/shops/{shopId}/reviews/recommend - 点评推荐
                .requestMatchers(HttpMethod.GET, "/api/shops/*/reviews/recommend").permitAll()
                
                // 其他所有接口：需要JWT认证
                .anyRequest().authenticated()
            )
            
            // 禁用HTTP Basic认证：只使用JWT认证
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // 添加JWT认证过滤器：在UsernamePasswordAuthenticationFilter之前执行
            // 这样可以在Spring Security的认证流程之前解析JWT token
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 配置密码编码器。
     * 
     * <p>使用BCrypt算法对密码进行哈希加密，这是目前最安全的密码加密方式之一。
     * BCrypt会自动生成随机盐值，每次加密的结果都不同，提高了安全性。
     * 
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

