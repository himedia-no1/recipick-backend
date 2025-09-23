package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credential_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", length = 20, nullable = false, unique = true)
    private String value;

    public enum Type {
        GOOGLE("GOOGLE"),
        NAVER("NAVER"),
        KAKAO("KAKAO");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}