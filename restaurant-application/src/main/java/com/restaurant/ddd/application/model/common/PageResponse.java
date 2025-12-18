package com.restaurant.ddd.application.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper
 * @param <T> Type of items in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    
    /**
     * Create PageResponse from items and pagination info
     */
    public static <T> PageResponse<T> of(List<T> items, int page, int size, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setItems(items);
        response.setPage(page);
        response.setSize(size);
        response.setTotal(total);
        response.setTotalPages((int) Math.ceil((double) total / size));
        return response;
    }
}
