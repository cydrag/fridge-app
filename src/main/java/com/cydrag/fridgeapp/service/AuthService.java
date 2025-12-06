package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.exception.UserAlreadyExistsException;
import com.cydrag.fridgeapp.model.RefreshToken;
import com.cydrag.fridgeapp.model.User;
import com.cydrag.fridgeapp.repository.UserRepository;
import com.cydrag.fridgeapp.security.FridgeUserDetails;
import com.cydrag.fridgeapp.security.JwtUtils;
import com.cydrag.fridgeapp.service.model.AuthResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResult register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use!");
        }

        User newUser = new User(email, passwordEncoder.encode(password));

        User savedUser = userRepository.save(newUser);

        String jwtAccessToken = jwtUtils.generateToken(savedUser.getId());
        RefreshToken refreshToken = refreshTokenService.createFirstRefreshToken(savedUser);

        return new AuthResult(jwtAccessToken, refreshToken.getToken(), jwtUtils.getJwtExpirationMs());
    }

    public AuthResult login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        FridgeUserDetails userDetails = (FridgeUserDetails) authentication.getPrincipal();

        if (userDetails == null || userDetails.getId() == null) {
            throw new RuntimeException("Auth principal or user ID is empty after login authentication.");
        }

        String jwt = jwtUtils.generateToken(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new AuthResult(jwt, refreshToken.getToken(), jwtUtils.getJwtExpirationMs());
    }
}
