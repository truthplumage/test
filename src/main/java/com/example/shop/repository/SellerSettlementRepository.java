package com.example.shop.repository;

import com.example.shop.entity.SellerSettlement;
import com.example.shop.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SellerSettlementRepository extends JpaRepository<SellerSettlement, UUID> {

    List<SellerSettlement> findByStatus(SettlementStatus status);

    List<SellerSettlement> findByStatusAndSellerId(SettlementStatus status, UUID sellerId);
}
