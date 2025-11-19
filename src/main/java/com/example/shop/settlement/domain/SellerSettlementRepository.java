package com.example.shop.settlement.domain;

import java.util.List;
import java.util.UUID;

public interface SellerSettlementRepository {

    SellerSettlement save(SellerSettlement settlement);

    List<SellerSettlement> findByStatus(SettlementStatus status);

    void saveAll(List<SellerSettlement> settlements);
}
