package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_levels")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", length = 20, nullable = false, unique = true)
    private String value;

    public enum Type {
        VERY_EASY("VERY_EASY"),
        EASY("EASY"),
        NORMAL("NORMAL"),
        HARD("HARD"),
        VERY_HARD("VERY_HARD");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}