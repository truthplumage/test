package com.example.shop.cart.infrastructure;

import com.example.shop.cart.domain.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemJpaRepository extends JpaRepository<CartItem, UUID> {

    Page<CartItem> findByMemberId(UUID memberId, Pageable pageable);

    Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId);
}
