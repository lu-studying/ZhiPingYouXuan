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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
            
            // 启用CORS跨域支持：允许前端（localhost:3000）访问后端API
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理：使用无状态模式，不创建HTTP Session
            // 这样每次请求都需要携带JWT token，适合RESTful API
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置接口访问权限
            // 细粒度权限：仅放行登录/注册和公开查询，其余需认证
            .authorizeHttpRequests(auth -> auth
                // 登录 / 注册
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/login", "/api/users/register").permitAll()

                // 公开查询（商家/点评）
                .requestMatchers(HttpMethod.GET, "/api/shops").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/shops/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/shops/*/reviews").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/shops/*/reviews/recommend").permitAll()

                // 仪表盘、用户管理、AI日志查询需要认证（管理端功能）
                // 注意：这些接口需要登录后才能访问，用于管理端数据展示

                // 其他请求需要认证
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

    /**
     * 配置CORS跨域资源共享。
     * 
     * <p>功能说明：
     * 1. 允许前端应用（localhost:3000）访问后端API（localhost:8080）
     * 2. 允许常用的HTTP方法（GET、POST、PUT、DELETE、OPTIONS等）
     * 3. 允许携带认证信息（如JWT token）
     * 4. 允许常用的请求头（Content-Type、Authorization等）
     * 
     * <p>安全说明：
     * - 在生产环境中，应该将 allowedOrigins 设置为实际的前端域名
     * - 不应该使用 "*" 通配符，这会允许任何域名访问
     * 
     * @return CorsConfigurationSource实例，用于配置CORS规则
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（前端地址）
        // 开发环境：
        // - 通过 Vite 直连：localhost:3000 / 127.0.0.1:3000
        // - 通过 Nginx 统一入口：localhost（80 端口）
        // 生产环境：应该设置为实际的前端域名，如 "https://yourdomain.com"
        // 允许的源（前端地址）
        // - Vite 直连：localhost:3000 / 127.0.0.1:3000
        // - Nginx 统一入口：localhost / 127.0.0.1
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost",
            "http://127.0.0.1"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.PATCH.name()
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        // 允许携带认证信息（如JWT token）
        // 设置为true后，前端可以在请求头中携带Authorization字段
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        // 浏览器会缓存OPTIONS请求的结果，减少重复请求
        configuration.setMaxAge(3600L);
        
        // 注册CORS配置：应用到所有API路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}

