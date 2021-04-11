package com.example.storeapp.config;

import com.example.storeapp.entity.InventoryEntity;
import com.example.storeapp.entity.ProductEntity;
import com.example.storeapp.repository.InventoryRepository;
import com.example.storeapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataSetupConfig {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    @PostConstruct
    public void setupData() {
        var productEntity1 = new ProductEntity();
        productEntity1.setName("Phone");
        productEntity1.setWeight(1L);
        productEntity1.setPrice(500L);
        productRepository.save(productEntity1);

        var productEntity2 = new ProductEntity();
        productEntity2.setName("Laptop");
        productEntity2.setWeight(3L);
        productEntity2.setPrice(1000L);
        productRepository.save(productEntity2);

        productRepository.findAll()
                         .forEach(productEntity -> log.error("{}", productEntity));

        var inventoryEntity1 = new InventoryEntity();
        inventoryEntity1.setProduct(productEntity1);
        inventoryEntity1.setQuantity(5L);
        inventoryRepository.save(inventoryEntity1);

        var inventoryEntity2 = new InventoryEntity();
        inventoryEntity2.setProduct(productEntity2);
        inventoryEntity2.setQuantity(3L);
        inventoryRepository.save(inventoryEntity2);

        inventoryRepository.findAll()
                           .forEach(inventoryEntity -> log.error("{}", inventoryEntity));
    }

}
