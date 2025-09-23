package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compartment_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompartmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", length = 20, nullable = false, unique = true)
    private String value;

    public enum Type {
        FREEZER("FREEZER"),      // 냉동
        REFRIGERATOR("REFRIGERATOR"), // 냉장
        ROOM_TEMPERATURE("ROOM_TEMPERATURE"); // 실온

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}