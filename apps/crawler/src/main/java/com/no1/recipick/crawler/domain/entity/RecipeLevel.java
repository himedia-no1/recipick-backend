package com.no1.recipick.crawler.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_levels")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", nullable = false, unique = true, length = 20)
    private String value;

    public enum Level {
        VERY_EASY("아무나"),
        EASY("초급"),
        NORMAL("중급"),
        HARD("고급"),
        VERY_HARD("신의경지");

        private final String koreanName;

        Level(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public static Level fromKoreanName(String koreanName) {
            for (Level level : values()) {
                if (level.koreanName.equals(koreanName)) {
                    return level;
                }
            }
            return null;
        }
    }
}