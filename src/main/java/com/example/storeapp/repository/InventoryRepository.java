package com.example.storeapp.repository;

import com.example.storeapp.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    
    Optional<InventoryEntity> findByProductId(Long productId);
    
}
