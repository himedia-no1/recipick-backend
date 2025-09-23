package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredient_states")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", length = 20, nullable = false, unique = true)
    private String value;

    public enum Type {
        FRESH("FRESH"),               // 신선함
        NEAR_EXPIRY("NEAR_EXPIRY"),   // 유통기한 임박
        EXPIRED("EXPIRED"),           // 유통기한 만료
        CONSUMED("CONSUMED"),         // 사용됨
        DISPOSED("DISPOSED");         // 폐기됨

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}