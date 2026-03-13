package com.demo.dp.service;

import com.demo.dp.domain.entity.User;
import com.demo.dp.mapper.UserMapper;
import com.demo.dp.mapper.UserTagMapper;
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
    private final UserTagMapper userTagMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, UserTagMapper userTagMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userTagMapper = userTagMapper;
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

    /**
     * 更新用户信息。
     * 
     * @param user 用户对象（包含需要更新的字段）
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户不存在或昵称已被使用时抛出
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        // 检查用户是否存在
        Optional<User> existingOpt = findById(user.getId());
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User existing = existingOpt.get();
        
        // 如果更新昵称，需要检查是否已被其他用户使用
        if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
            String newNickname = user.getNickname().trim();
            // 如果新昵称与当前昵称不同，需要查重
            if (!newNickname.equals(existing.getNickname())) {
                User duplicateUser = userMapper.findByNickname(newNickname, user.getId());
                if (duplicateUser != null) {
                    throw new IllegalArgumentException("该昵称已被使用，请换一个");
                }
            }
            existing.setNickname(newNickname);
        }
        
        if (user.getAvatar() != null) {
            existing.setAvatar(user.getAvatar());
        }
        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }
        
        // 更新 updatedAt
        existing.setUpdatedAt(LocalDateTime.now());
        
        // 执行更新
        userMapper.update(existing);
        
        return existing;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        Optional<User> userOpt = findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (userTagMapper != null) {
            userTagMapper.deleteByUserId(id);
        }
        userMapper.delete(id);
    }
}

