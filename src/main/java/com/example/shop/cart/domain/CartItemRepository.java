package com.example.shop.cart.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    Optional<CartItem> findById(UUID id);

    Page<CartItem> findByMemberId(UUID memberId, Pageable pageable);

    Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId);

    void deleteById(UUID id);
}
