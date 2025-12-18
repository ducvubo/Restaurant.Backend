package com.restaurant.ddd.application.model.common;

import lombok.Data;

/**
 * Base class for paginated list requests with sorting support
 */
@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy;
    private String sortDirection = "DESC"; // ASC or DESC
    
    /**
     * Get page number (0-based for Spring Data)
     */
    public int getPageZeroBased() {
        return (page != null && page > 0) ? page - 1 : 0;
    }
    
    /**
     * Get safe size value
     */
    public int getSafeSize() {
        if (size == null || size <= 0) {
            return 10;
        }
        // Max 100 items per page
        return Math.min(size, 100);
    }
    
    /**
     * Get safe sort direction
     */
    public String getSafeSortDirection() {
        if (sortDirection == null) {
            return "DESC";
        }
        String upper = sortDirection.toUpperCase();
        return "ASC".equals(upper) ? "ASC" : "DESC";
    }
}
