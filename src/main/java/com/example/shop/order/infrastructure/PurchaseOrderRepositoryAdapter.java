package com.example.shop.order.infrastructure;

import com.example.shop.order.domain.PurchaseOrder;
import com.example.shop.order.domain.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PurchaseOrderRepositoryAdapter implements PurchaseOrderRepository {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;

    public PurchaseOrderRepositoryAdapter(PurchaseOrderJpaRepository purchaseOrderJpaRepository) {
        this.purchaseOrderJpaRepository = purchaseOrderJpaRepository;
    }

    @Override
    public PurchaseOrder save(PurchaseOrder order) {
        return purchaseOrderJpaRepository.save(order);
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID id) {
        return purchaseOrderJpaRepository.findById(id);
    }

    @Override
    public Page<PurchaseOrder> findAll(Pageable pageable) {
        return purchaseOrderJpaRepository.findAll(pageable);
    }
}
