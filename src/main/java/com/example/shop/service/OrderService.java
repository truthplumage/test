package com.example.shop.service;

import com.example.shop.common.ResponseEntity;
import com.example.shop.entity.dto.OrderCommand;
import com.example.shop.entity.dto.OrderInfo;
import com.example.shop.entity.PurchaseOrder;
import com.example.shop.repository.PurchaseOrderRepository;
import com.example.shop.product.domain.Product;
import com.example.shop.product.domain.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final PurchaseOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(PurchaseOrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<OrderInfo> create(OrderCommand command) {
        if (command.productId() == null || command.memberId() == null) {
            throw new IllegalArgumentException("productId and memberId are required");
        }
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + command.productId()));

        PurchaseOrder order = PurchaseOrder.create(
                product.getId(),
                product.getSellerId(),
                command.memberId(),
                product.getPrice()
        );
        PurchaseOrder saved = orderRepository.save(order);
        return new ResponseEntity<>(HttpStatus.CREATED.value(), OrderInfo.from(saved), 1);
    }

    public ResponseEntity<List<OrderInfo>> findAll(Pageable pageable) {
        Page<PurchaseOrder> page = orderRepository.findAll(pageable);
        List<OrderInfo> infos = page.stream().map(OrderInfo::from).toList();
        return new ResponseEntity<>(HttpStatus.OK.value(), infos, page.getTotalElements());
    }

    public PurchaseOrder findEntity(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public void markPaid(PurchaseOrder order) {
        order.markPaid();
        orderRepository.save(order);
    }
}
