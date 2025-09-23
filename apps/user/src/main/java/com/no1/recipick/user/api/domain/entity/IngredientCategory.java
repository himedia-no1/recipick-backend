package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredient_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", length = 20, nullable = false, unique = true)
    private String value;

    public enum Type {
        VEGETABLE("VEGETABLE"),     // 채소
        FRUIT("FRUIT"),             // 과일
        MEAT("MEAT"),               // 육류
        SEAFOOD("SEAFOOD"),         // 해산물
        DAIRY("DAIRY"),             // 유제품
        GRAIN("GRAIN"),             // 곡류
        SEASONING("SEASONING"),     // 조미료
        PROCESSED("PROCESSED"),     // 가공식품
        BEVERAGE("BEVERAGE"),       // 음료
        ETC("ETC");                 // 기타

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}