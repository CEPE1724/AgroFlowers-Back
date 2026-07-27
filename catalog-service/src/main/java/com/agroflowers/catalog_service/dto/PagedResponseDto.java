package com.agroflowers.catalog_service.dto;

import java.util.List;

public record PagedResponseDto<T>(
        List<T> items,
        long total,
        int page,
        int pageSize
) {
}
