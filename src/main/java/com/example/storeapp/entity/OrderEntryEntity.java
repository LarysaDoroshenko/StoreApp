package com.example.storeapp.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "order_entry")
@Data
@ToString(exclude = {
        "product"
})
public class OrderEntryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;
    
}
