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
public class PagedResponse<T> {

    private List<T> content;
    private PageInfo pageInfo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PageInfo {
        private Integer totalCount;
        private Integer currentPage;
        private Integer totalPages;
    }

    public static <T> PagedResponse<T> of(List<T> content, int totalCount, int currentPage, int totalPages) {
        return PagedResponse.<T>builder()
                .content(content)
                .pageInfo(PageInfo.builder()
                        .totalCount(totalCount)
                        .currentPage(currentPage)
                        .totalPages(totalPages)
                        .build())
                .build();
    }
}