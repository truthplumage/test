package com.example.shop.seller.presentation.dto;

import com.example.shop.seller.application.dto.SellerCommand;

/**
 * 판매자 API에서 사용되는 요청 DTO.
 */
public record SellerRequest(
        String companyName,
        String representativeName,
        String email,
        String phone,
        String businessNumber,
        String address,
        String status
) {

    public SellerCommand toCommand() {
        return new SellerCommand(companyName, representativeName, email, phone, businessNumber, address, status);
    }
}
