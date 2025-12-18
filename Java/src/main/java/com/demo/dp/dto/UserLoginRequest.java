package com.demo.dp.dto;

/**
 * 登录/注册请求 DTO。
 * mobileOrEmail：可以是手机号或邮箱，由后端自行判断校验。
 */
public class UserLoginRequest {
    private String mobileOrEmail;
    private String password;

    public String getMobileOrEmail() { return mobileOrEmail; }
    public void setMobileOrEmail(String mobileOrEmail) { this.mobileOrEmail = mobileOrEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

