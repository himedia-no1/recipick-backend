package com.no1.recipick.user.api.security;

import com.no1.recipick.user.api.domain.entity.CredentialType;
import com.no1.recipick.user.api.domain.entity.User;
import com.no1.recipick.user.api.domain.repository.CredentialTypeRepository;
import com.no1.recipick.user.api.domain.repository.UserRepository;
import com.no1.recipick.user.api.security.oauth2.OAuth2UserInfo;
import com.no1.recipick.user.api.security.oauth2.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CredentialTypeRepository credentialTypeRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        if (!StringUtils.hasText(oauth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        CredentialType credentialType = credentialTypeRepository.findByValue(registrationId.toUpperCase())
                .orElseThrow(() -> new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId));

        Optional<User> userOptional = userRepository.findBySocialIdentityAndCredentialTypeAndIsDeletedFalse(
                oauth2UserInfo.getId(), credentialType);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("Existing user found: {}", user.getId());
        } else {
            user = registerNewUser(oauth2UserInfo, credentialType);
            log.info("New user registered: {}", user.getId());
        }

        return CustomUserPrincipal.create(user, oauth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserInfo oauth2UserInfo, CredentialType credentialType) {
        User user = User.builder()
                .credentialType(credentialType)
                .socialIdentity(oauth2UserInfo.getId())
                .email(oauth2UserInfo.getEmail())
                .nickname(generateUniqueNickname(oauth2UserInfo.getName()))
                .profileImage(oauth2UserInfo.getImageUrl())
                .isDeleted(false)
                .build();

        return userRepository.save(user);
    }

    private String generateUniqueNickname(String name) {
        String baseNickname = StringUtils.hasText(name) ? name : "사용자";
        String nickname = baseNickname;
        int counter = 1;

        while (userRepository.existsByNicknameAndIsDeletedFalse(nickname)) {
            nickname = baseNickname + counter;
            counter++;
        }

        return nickname;
    }
}