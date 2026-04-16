package com.chaman.auth.controller;

import com.chaman.auth.dto.AuthRequest;
import com.chaman.auth.dto.AuthResponse;
import com.chaman.auth.model.User;
import com.chaman.auth.repository.UserRepository;
import com.chaman.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String loginId = request.getUsername();
        if (loginId == null || loginId.isEmpty()) {
            loginId = request.getEmail();
        }
        
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginId,
                        request.getPassword()
                )
        );
        String token = jwtUtil.generateToken(loginId);
        return new AuthResponse(token, token);
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }
}
