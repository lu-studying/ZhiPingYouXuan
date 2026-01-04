package com.demo.dp.service;

import com.demo.dp.domain.entity.User;
import com.demo.dp.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现类：实现用户相关的业务逻辑。
 * 
 * 注意：真实环境需要密码哈希、重复校验、异常处理、JWT 颁发等。
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 简单注册示例：仅保存手机号/邮箱与密码哈希。
     * 
     * @param mobileOrEmail 手机号或邮箱
     * @param password 密码（明文）
     * @return 注册成功的用户对象
     * @throws IllegalArgumentException 重复用户时抛出
     */
    @Override
    @Transactional
    public User register(String mobileOrEmail, String password) {
        // 重复校验
        if (mobileOrEmail != null && mobileOrEmail.contains("@")) {
            if (userMapper.findByEmail(mobileOrEmail) != null) {
                throw new IllegalArgumentException("该邮箱已存在，请前往登录");
            }
        } else {
            if (userMapper.findByMobile(mobileOrEmail) != null) {
                throw new IllegalArgumentException("该电话号码已存在，请前往登录");
            }
        }
        User u = new User();
        // 简单分支：包含 @ 认为是邮箱，否则手机号。真实场景需更严格校验。
        if (mobileOrEmail != null && mobileOrEmail.contains("@")) {
            u.setEmail(mobileOrEmail);
        } else {
            u.setMobile(mobileOrEmail);
        }
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setStatus(1);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(u);
        return u;
    }

    @Override
    public Optional<User> findByMobile(String mobile) {
        User user = userMapper.findByMobile(mobile);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = userMapper.findByEmail(email);
        return Optional.ofNullable(user);
    }

    @Override
    public java.util.List<User> listUsers(int page, int size, String keyword) {
        int offset = page * size;
        return userMapper.findByConditions(offset, size, keyword);
    }

    @Override
    public long countUsers(String keyword) {
        return userMapper.countByConditions(keyword);
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = userMapper.findById(id);
        return Optional.ofNullable(user);
    }
}

