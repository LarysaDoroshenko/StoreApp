package com.example.storeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInputDto {
    
    private Long productId;
    private Long quantity;
    
}
