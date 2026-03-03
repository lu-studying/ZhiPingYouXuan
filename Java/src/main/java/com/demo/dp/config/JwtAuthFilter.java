package com.demo.dp.config;

import com.demo.dp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简单的 JWT 认证过滤器：
 * - 从 Authorization: Bearer <token> 解析
 * - 将 userId 写入 Authentication，便于后续获取
 * - 略去异常分类，解析失败则忽略，让后续安全配置处理
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                String sub = claims.getSubject();
                if (sub != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 将 userId 放入 principal，并根据 token 中的 roles 构建权限列表
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    Object rolesObj = claims.get("roles");
                    if (rolesObj instanceof List<?> list) {
                        for (Object roleCodeObj : list) {
                            if (roleCodeObj != null) {
                                String roleCode = roleCodeObj.toString();
                                // Spring Security 约定：角色前缀为 ROLE_
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
                            }
                        }
                    }
                    // 兜底：如果没有任何角色，给一个普通用户角色，避免空权限列表
                    if (authorities.isEmpty()) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            sub,
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // 解析失败直接忽略，后续链路可根据安全规则拒绝
            }
        }
        filterChain.doFilter(request, response);
    }
}

