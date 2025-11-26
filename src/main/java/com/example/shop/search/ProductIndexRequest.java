package com.example.shop.search;

// 상품 색인 요청 DTO (id/updatedAt은 서버에서 자동 생성)
public record ProductIndexRequest(
    String name,
    String brand,
    String category,
    Integer price
) {
}
