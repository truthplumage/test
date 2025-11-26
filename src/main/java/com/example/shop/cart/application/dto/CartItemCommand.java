package com.example.shop.cart.application.dto;

import java.util.UUID;

public record CartItemCommand(
        UUID memberId,
        UUID productId,
        int quantity
) {
}
