package com.no1.recipick.user.api.security;

import com.no1.recipick.user.api.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements OAuth2User, UserDetails {

    private Integer id;
    private String email;
    private String nickname;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static CustomUserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                authorities,
                null
        );
    }

    public static CustomUserPrincipal create(User user, Map<String, Object> attributes) {
        CustomUserPrincipal userPrincipal = CustomUserPrincipal.create(user);
        userPrincipal.attributes = attributes;
        return userPrincipal;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}