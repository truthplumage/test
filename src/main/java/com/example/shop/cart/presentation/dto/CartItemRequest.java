package com.example.shop.cart.presentation.dto;

import com.example.shop.cart.application.dto.CartItemCommand;

import java.util.UUID;

public record CartItemRequest(
        String memberId,
        String productId,
        Integer quantity
) {

    public CartItemCommand toCommand() {
        UUID member = memberId != null ? UUID.fromString(memberId) : null;
        UUID product = productId != null ? UUID.fromString(productId) : null;
        int qty = quantity != null ? quantity : 0;
        return new CartItemCommand(member, product, qty);
    }
}
