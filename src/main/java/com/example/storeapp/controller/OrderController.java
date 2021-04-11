package com.example.storeapp.controller;

import com.example.storeapp.dto.OrderInputDto;
import com.example.storeapp.dto.OrderOutputDto;
import com.example.storeapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderOutputDto> createOrder(@RequestBody OrderInputDto orderInputDto) {
        Long orderId = orderService.processOrder(orderInputDto);

        return ResponseEntity.ok(new OrderOutputDto(orderId));
    }

}
