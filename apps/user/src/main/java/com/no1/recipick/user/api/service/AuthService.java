package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.User;
import com.no1.recipick.user.api.domain.repository.UserRepository;
import com.no1.recipick.user.api.dto.request.TokenRefreshRequest;
import com.no1.recipick.user.api.dto.request.UserInitialRequest;
import com.no1.recipick.user.api.dto.response.AuthResponse;
import com.no1.recipick.user.api.dto.response.TokenResponse;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import com.no1.recipick.user.api.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse createAuthResponse(User user, boolean isNewUser) {
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()));

        log.info("Generated tokens for user: {}, isNewUser: {}", user.getId(), isNewUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .build();
    }

    public TokenResponse refreshAccessToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("리프레시 토큰이 유효하지 않거나 만료되었습니다.");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findByIdAndIsDeletedFalse(Integer.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
        long expiresIn = jwtTokenProvider.getAccessTokenExpiration();

        log.info("Refreshed access token for user: {}", user.getId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(expiresIn)
                .build();
    }

    public void setupInitialUserInfo(Integer userId, UserInitialRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 검사
        if (userRepository.existsByNicknameAndIsDeletedFalse(request.getNickname())) {
            throw new BusinessException("이미 존재하는 닉네임입니다.");
        }

        user.updateProfile(request.getNickname(), request.getProfileImage());
        log.info("Initial user info setup completed for user: {}", user.getId());
    }

    public void logout(Integer userId) {
        // JWT는 stateless하므로 서버에서 토큰을 무효화할 수 없음
        // 클라이언트에서 토큰을 삭제하도록 응답
        log.info("User logged out: {}", userId);
    }
}