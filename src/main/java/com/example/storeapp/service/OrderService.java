package com.example.storeapp.service;

import com.example.storeapp.dto.OrderInputDto;
import com.example.storeapp.entity.*;
import com.example.storeapp.repository.InventoryRepository;
import com.example.storeapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Transactional
    public Long processOrder(OrderInputDto orderInputDto) {
        Long productId = orderInputDto.getProductId();
        ProductEntity productEntity = productRepository.findById(productId)
                                                       .orElseThrow(() -> new RuntimeException(String.format("%s not found by id: %s", ProductEntity.class.getSimpleName(), productId)));

        reentrantLock.lock();
        try {
            InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productId)
                                                                 .orElseThrow(() -> new RuntimeException(String.format("%s not found by productId: %s", InventoryEntity.class.getSimpleName(), productId)));

            var orderEntryEntity = new OrderEntryEntity();
            orderEntryEntity.setProduct(productEntity);
            orderEntryEntity.setQuantity(orderInputDto.getQuantity());
            entityManager.persist(orderEntryEntity);

            var orderEntity = new OrderEntity();
            orderEntity.setOrderEntry(orderEntryEntity);

            if (inventoryEntity.getQuantity() >= orderInputDto.getQuantity()) {
                orderEntity.setStatus(OrderStatus.SUCCESS);

                inventoryEntity.setQuantity(inventoryEntity.getQuantity() - orderInputDto.getQuantity());
                entityManager.persist(inventoryEntity);
            } else {
                orderEntity.setStatus(OrderStatus.FAILURE);
            }
            entityManager.persist(orderEntity);

            return orderEntity.getId();
        } finally {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        reentrantLock.unlock();
                    }
                });
            } else {
                reentrantLock.unlock();
            }
        }
    }

}
