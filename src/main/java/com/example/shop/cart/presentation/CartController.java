package com.example.shop.cart.presentation;

import com.example.shop.cart.application.CartService;
import com.example.shop.cart.application.dto.CartItemInfo;
import com.example.shop.cart.presentation.dto.CartItemRequest;
import com.example.shop.cart.presentation.dto.CartItemUpdateRequest;
import com.example.shop.common.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.v1}/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "장바구니 담기", description = "상품을 장바구니에 담는다. 이미 담긴 상품이면 수량을 누적한다.")
    @PostMapping
    public ResponseEntity<CartItemInfo> add(@RequestBody CartItemRequest request) {
        return cartService.add(request.toCommand());
    }

    @Operation(summary = "회원 장바구니 조회", description = "회원별 장바구니 목록을 페이지 단위로 조회한다.")
    @GetMapping
    public ResponseEntity<List<CartItemInfo>> findByMember(@RequestParam("memberId") UUID memberId,
                                                           Pageable pageable) {
        return cartService.findByMember(memberId, pageable);
    }

    @Operation(summary = "장바구니 수량 수정", description = "장바구니 아이템의 수량을 변경한다.")
    @PutMapping("{id}")
    public ResponseEntity<CartItemInfo> updateQuantity(@PathVariable("id") UUID id,
                                                       @RequestBody CartItemUpdateRequest request) {
        return cartService.update(id, request.toCommand());
    }

    @Operation(summary = "장바구니 아이템 삭제", description = "장바구니에서 아이템을 삭제한다.")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        return cartService.delete(id);
    }
}
