package com.example.shop.cart.application;

import com.example.shop.cart.application.dto.CartItemCommand;
import com.example.shop.cart.application.dto.CartItemInfo;
import com.example.shop.cart.application.dto.CartItemUpdateCommand;
import com.example.shop.cart.domain.CartItem;
import com.example.shop.cart.domain.CartItemRepository;
import com.example.shop.common.ResponseEntity;
import com.example.shop.product.domain.Product;
import com.example.shop.product.domain.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<CartItemInfo> add(CartItemCommand command) {
        validateCreateCommand(command);
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + command.productId()));

        Optional<CartItem> existing = cartItemRepository.findByMemberIdAndProductId(command.memberId(), product.getId());
        boolean existed = existing.isPresent();

        CartItem cartItem = existing
                .map(item -> {
                    item.addQuantity(command.quantity());
                    return item;
                })
                .orElseGet(() -> CartItem.create(command.memberId(), product.getId(), command.quantity()));

        CartItem saved = cartItemRepository.save(cartItem);
        HttpStatus status = existed ? HttpStatus.OK : HttpStatus.CREATED;
        return new ResponseEntity<>(status.value(), CartItemInfo.from(saved), 1);
    }

    public ResponseEntity<List<CartItemInfo>> findByMember(UUID memberId, Pageable pageable) {
        Page<CartItem> page = cartItemRepository.findByMemberId(memberId, pageable);
        List<CartItemInfo> infos = page.stream().map(CartItemInfo::from).toList();
        return new ResponseEntity<>(HttpStatus.OK.value(), infos, page.getTotalElements());
    }

    public ResponseEntity<CartItemInfo> update(UUID cartItemId, CartItemUpdateCommand command) {
        if (command.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));
        cartItem.updateQuantity(command.quantity());
        CartItem saved = cartItemRepository.save(cartItem);
        return new ResponseEntity<>(HttpStatus.OK.value(), CartItemInfo.from(saved), 1);
    }

    public ResponseEntity<Void> delete(UUID cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT.value(), null, 0);
    }

    private void validateCreateCommand(CartItemCommand command) {
        if (command.memberId() == null || command.productId() == null) {
            throw new IllegalArgumentException("memberId and productId are required");
        }
        if (command.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}
