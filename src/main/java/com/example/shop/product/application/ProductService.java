package com.example.shop.product.application;

import com.example.shop.common.ResponseEntity;
import com.example.shop.product.application.dto.ProductCommand;
import com.example.shop.product.application.dto.ProductInfo;
import com.example.shop.product.domain.Product;
import com.example.shop.product.domain.ProductRepository;
import com.example.shop.seller.domain.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    public ProductService(ProductRepository productRepository,
                          SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    public ResponseEntity<List<ProductInfo>> findAll(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductInfo> products = page.stream()
                .map(ProductInfo::from)
                .toList();
        return new ResponseEntity<>(HttpStatus.OK.value(), products, page.getTotalElements());
    }

    public ResponseEntity<ProductInfo> create(ProductCommand command) {
        if (command.sellerId() == null) {
            throw new IllegalArgumentException("sellerId is required");
        }
        sellerRepository.findById(command.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + command.sellerId()));
        UUID operator = command.operatorId() != null ? command.operatorId() : command.sellerId();
        Product product = Product.create(
                command.sellerId(),
                command.name(),
                command.description(),
                command.price(),
                command.stock(),
                command.status(),
                operator
        );
        Product saved = productRepository.save(product);
        return new ResponseEntity<>(HttpStatus.CREATED.value(), ProductInfo.from(saved), 1);
    }

    public ResponseEntity<ProductInfo> update(UUID productId, ProductCommand command) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        UUID operator = command.operatorId() != null ? command.operatorId() : product.getModifyId();
        product.update(
                command.name(),
                command.description(),
                command.price(),
                command.stock(),
                command.status(),
                operator
        );
        Product updated = productRepository.save(product);
        return new ResponseEntity<>(HttpStatus.OK.value(), ProductInfo.from(updated), 1);
    }

    public ResponseEntity<Void> delete(UUID productId) {
        productRepository.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT.value(), null, 0);
    }
}
