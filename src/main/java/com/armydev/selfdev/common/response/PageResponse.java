package com.armydev.selfdev.common.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> data;
    private final Meta meta;

    public PageResponse(List<T> data, long totalElements, int page, int size) {
        this.data = data;
        this.meta = new Meta(page, size, totalElements, (int) Math.ceil((double) totalElements / size));
    }

    @Getter
    public static class Meta {
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        public Meta(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
