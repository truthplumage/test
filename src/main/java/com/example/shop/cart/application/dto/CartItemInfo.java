package com.example.shop.cart.application.dto;

import com.example.shop.cart.domain.CartItem;

import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemInfo(
        UUID id,
        UUID memberId,
        UUID productId,
        int quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CartItemInfo from(CartItem item) {
        return new CartItemInfo(
                item.getId(),
                item.getMemberId(),
                item.getProductId(),
                item.getQuantity(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
