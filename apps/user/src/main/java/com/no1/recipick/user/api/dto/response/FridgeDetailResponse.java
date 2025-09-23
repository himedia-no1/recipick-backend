package com.no1.recipick.user.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FridgeDetailResponse {

    private Integer fridgeId;
    private String name;
    private String memo;
    private Boolean isDefault;
    private Boolean isFavorite;
    private UserSummaryResponse owner;
    private List<UserSummaryResponse> members;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserSummaryResponse {
        private Integer userId;
        private String nickname;
    }
}