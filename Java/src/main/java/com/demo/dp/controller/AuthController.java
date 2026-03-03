package com.demo.dp.controller;

import com.demo.dp.domain.entity.User;
import com.demo.dp.dto.AuthResponse;
import com.demo.dp.dto.UserLoginRequest;
import com.demo.dp.mapper.RoleMapper;
import com.demo.dp.service.UserService;
import com.demo.dp.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关接口占位。
 * 真实环境需：
 * - 参数校验
 * - 密码哈希与校验
 * - JWT 颁发与刷新
 * - 异常与返回码规范
 */
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          RoleMapper roleMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserLoginRequest req) {
        // 简单校验
        if (req.getMobileOrEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        User saved = userService.register(req.getMobileOrEmail(), req.getPassword());
        // 查询用户角色列表，如果没有记录则默认给一个 USER 角色，方便前台使用
        java.util.List<String> roles = roleMapper.findRoleCodesByUserId(saved.getId());
        if (roles == null || roles.isEmpty()) {
            roles = java.util.List.of("USER");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", saved.getId());
        claims.put("roles", roles);
        String token = jwtUtil.generateToken(saved.getId(), claims);
        log.info("register success, userId={}, roles={}, token={}", saved.getId(), roles, token);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest req) {
        if (req.getMobileOrEmail() == null || req.getPassword() == null) {
            return error(400, "账号或密码未填写");
        }
        User user = req.getMobileOrEmail().contains("@")
                ? userService.findByEmail(req.getMobileOrEmail()).orElse(null)
                : userService.findByMobile(req.getMobileOrEmail()).orElse(null);
        if (user == null) {
            return error(401, "账号不存在，请先注册");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return error(401, "账号或密码错误");
        }
        // 查询角色列表（如 ADMIN / MERCHANT / USER 等），为空则默认 USER
        java.util.List<String> roles = roleMapper.findRoleCodesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            roles = java.util.List.of("USER");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("roles", roles);
        String token = jwtUtil.generateToken(user.getId(), claims);
        log.info("login success, userId={}, roles={}, token={}", user.getId(), roles, token);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    private ResponseEntity<Map<String, Object>> error(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", status);
        body.put("message", message);
        log.warn("auth error: status={}, message={}", status, message);
        return ResponseEntity.status(status).body(body);
    }
}

