package com.chaman.auth.security;

import com.chaman.auth.model.User;
import com.chaman.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${FRONTEND_URL:http://localhost:4200}")
    private String frontendUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            // Handle edge case where email is not available
            email = "user" + UUID.randomUUID().toString() + "@example.com";
        }

        // Check if user exists by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Register new user
            user = new User();
            user.setEmail(email);
            // We use email local-part as username fallback, or just name
            String baseUsername = (name != null && !name.isEmpty()) ? name.replace(" ", "") : email.split("@")[0];
            user.setUsername(baseUsername + UUID.randomUUID().toString().substring(0, 4)); 
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        
        // Redirect to Frontend callback
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("accessToken", token)
                .queryParam("refreshToken", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
