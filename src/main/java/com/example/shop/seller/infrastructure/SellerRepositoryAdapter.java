package com.example.shop.seller.infrastructure;

import com.example.shop.seller.domain.Seller;
import com.example.shop.seller.domain.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SellerRepositoryAdapter implements SellerRepository {

    private final SellerJpaRepository sellerJpaRepository;

    public SellerRepositoryAdapter(SellerJpaRepository sellerJpaRepository) {
        this.sellerJpaRepository = sellerJpaRepository;
    }

    @Override
    public Page<Seller> findAll(Pageable pageable) {
        return sellerJpaRepository.findAll(pageable);
    }

    @Override
    public Optional<Seller> findById(UUID id) {
        return sellerJpaRepository.findById(id);
    }

    @Override
    public Seller save(Seller seller) {
        return sellerJpaRepository.save(seller);
    }

    @Override
    public void deleteById(UUID id) {
        sellerJpaRepository.deleteById(id);
    }
}
