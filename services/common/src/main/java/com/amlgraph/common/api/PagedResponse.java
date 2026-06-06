package com.amlgraph.common.api;

import java.util.List;

public record PagedResponse<T>(List<T> data, Pagination pagination) {
    public static <T> PagedResponse<T> of(List<T> data, int page, int size, long totalElements, int totalPages) {
        return new PagedResponse<>(data, new Pagination(page, size, totalElements, totalPages));
    }

    public record Pagination(int page, int size, long totalElements, int totalPages) {}
}
