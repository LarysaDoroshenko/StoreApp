package com.example.storeapp.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "inventory")
@Data
@ToString(exclude = {
        "product"
})
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long quantity;
    
    @OneToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

}
