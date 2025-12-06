package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.exception.TokenRefreshException;
import com.cydrag.fridgeapp.model.RefreshToken;
import com.cydrag.fridgeapp.model.User;
import com.cydrag.fridgeapp.repository.RefreshTokenRepository;
import com.cydrag.fridgeapp.repository.UserRepository;
import com.cydrag.fridgeapp.security.JwtUtils;
import com.cydrag.fridgeapp.service.model.AuthResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${app.security.jwtRefreshExpirationMs}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public RefreshToken createFirstRefreshToken(User user) {
        String opaqueToken = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusMillis(refreshTokenDurationMs);

        RefreshToken token = new RefreshToken(user, opaqueToken, expiry);
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        refreshTokenRepository.deleteByUser(userRepository.getReferenceById(userId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found during token creation"));
        String opaqueToken = UUID.randomUUID().toString();
        Instant tokenExpiryTimestamp = Instant.now().plusMillis(refreshTokenDurationMs);

        RefreshToken refreshToken = new RefreshToken(user, opaqueToken, tokenExpiryTimestamp);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public AuthResult processRefreshToken(String requestToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(requestToken);

        if (tokenOpt.isEmpty()) {
            log.warn("Refresh Token Reuse Attempt or Invalid Token: {}", requestToken);
            throw new TokenRefreshException("Refresh token is invalid or expired!");
        }

        RefreshToken token = tokenOpt.get();

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);

            log.info("Refresh token expired: {}", requestToken);

            throw new TokenRefreshException("Refresh token is invalid or expired!");
        }

        refreshTokenRepository.delete(token);

        UUID userId = token.getUser().getId();

        RefreshToken newRefreshToken = createRefreshToken(userId);
        String newAccessToken = jwtUtils.generateToken(userId);

        return new AuthResult(newAccessToken, newRefreshToken.getToken(), jwtUtils.getJwtExpirationMs());
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
