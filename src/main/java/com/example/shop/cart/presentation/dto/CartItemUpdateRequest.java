package com.example.shop.cart.presentation.dto;

import com.example.shop.cart.application.dto.CartItemUpdateCommand;

public record CartItemUpdateRequest(
        Integer quantity
) {

    public CartItemUpdateCommand toCommand() {
        int qty = quantity != null ? quantity : 0;
        return new CartItemUpdateCommand(qty);
    }
}
