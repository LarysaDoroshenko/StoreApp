package com.example.storeapp.repository;

import com.example.storeapp.entity.OrderEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntryRepository extends JpaRepository<OrderEntryEntity, Long> {
}
