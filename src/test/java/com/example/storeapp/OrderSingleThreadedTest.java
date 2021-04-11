package com.example.storeapp;

import com.example.storeapp.dto.OrderInputDto;
import com.example.storeapp.dto.OrderOutputDto;
import com.example.storeapp.entity.InventoryEntity;
import com.example.storeapp.entity.OrderEntity;
import com.example.storeapp.entity.OrderStatus;
import com.example.storeapp.repository.InventoryRepository;
import com.example.storeapp.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/populate-products.sql",
        config = @SqlConfig(transactionMode = ISOLATED),
        executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/delete-products.sql",
        config = @SqlConfig(transactionMode = ISOLATED),
        executionPhase = AFTER_TEST_METHOD)
class OrderSingleThreadedTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    InventoryRepository inventoryRepository;

    @Test
    void whenOrderStatusIsSuccessThenProductQuantityIsDecreased() {
        var orderInputDto = new OrderInputDto();
        orderInputDto.setProductId(1L);
        orderInputDto.setQuantity(3L);

        ResponseEntity<OrderOutputDto> responseEntity = restTemplate.postForEntity("http://{host}:{port}/order/create", orderInputDto, OrderOutputDto.class, "localhost", port);

        OrderOutputDto body = responseEntity.getBody();

        OrderEntity orderEntity = orderRepository.getOne(body.getOrderId());
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.SUCCESS);

        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(1L).get();
        assertThat(inventoryEntity.getQuantity()).isEqualTo(2L);
    }

    @Test
    void whenOrderStatusIsFailureThenProductQuantityIsUnchanged() {
        var orderInputDto = new OrderInputDto();
        orderInputDto.setProductId(1L);
        orderInputDto.setQuantity(6L);

        ResponseEntity<OrderOutputDto> responseEntity = restTemplate.postForEntity("http://{host}:{port}/order/create", orderInputDto, OrderOutputDto.class, "localhost", port);

        OrderOutputDto body = responseEntity.getBody();

        OrderEntity orderEntity = orderRepository.getOne(body.getOrderId());
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.FAILURE);

        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(1L).get();
        assertThat(inventoryEntity.getQuantity()).isEqualTo(5L);
    }
}