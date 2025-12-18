package com.demo.dp.dto;

/**
 * 登录/注册返回 DTO，携带 JWT。
 */
public class AuthResponse {
    private String token;

    public AuthResponse() {}
    public AuthResponse(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

