package com.example.shop.cart.infrastructure;

import com.example.shop.cart.domain.CartItem;
import com.example.shop.cart.domain.CartItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CartItemRepositoryAdapter implements CartItemRepository {

    private final CartItemJpaRepository cartItemJpaRepository;

    public CartItemRepositoryAdapter(CartItemJpaRepository cartItemJpaRepository) {
        this.cartItemJpaRepository = cartItemJpaRepository;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        return cartItemJpaRepository.save(cartItem);
    }

    @Override
    public Optional<CartItem> findById(UUID id) {
        return cartItemJpaRepository.findById(id);
    }

    @Override
    public Page<CartItem> findByMemberId(UUID memberId, Pageable pageable) {
        return cartItemJpaRepository.findByMemberId(memberId, pageable);
    }

    @Override
    public Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId) {
        return cartItemJpaRepository.findByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public void deleteById(UUID id) {
        cartItemJpaRepository.deleteById(id);
    }
}
