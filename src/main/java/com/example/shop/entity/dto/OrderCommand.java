package com.example.shop.entity.dto;

import java.util.UUID;

public record OrderCommand(
        UUID productId,
        UUID memberId
) {
}
