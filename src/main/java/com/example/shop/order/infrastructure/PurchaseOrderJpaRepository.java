package com.example.shop.order.infrastructure;

import com.example.shop.order.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrder, UUID> {
}
