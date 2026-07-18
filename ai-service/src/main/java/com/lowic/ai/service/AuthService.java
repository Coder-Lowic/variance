package com.lowic.ai.service;

import com.lowic.ai.dto.AuthResponse;
import com.lowic.ai.dto.LoginRequest;
import com.lowic.ai.dto.RegisterRequest;
import com.lowic.ai.entity.User;
import com.lowic.ai.config.JwtUtil;
import com.lowic.ai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在: " + request.username());
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email()
        );
        userRepository.save(user);
        log.info("Registered new user: {}", request.username());

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.of(token, user.getUsername());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        log.info("User logged in: {}", request.username());
        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.of(token, user.getUsername());
    }
}
