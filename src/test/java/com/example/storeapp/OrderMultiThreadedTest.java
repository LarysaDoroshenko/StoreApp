package com.example.storeapp;

import com.example.storeapp.dto.OrderInputDto;
import com.example.storeapp.dto.OrderOutputDto;
import com.example.storeapp.entity.InventoryEntity;
import com.example.storeapp.entity.OrderEntity;
import com.example.storeapp.repository.InventoryRepository;
import com.example.storeapp.repository.OrderRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
class OrderMultiThreadedTest {

    static final int NUMBER_OF_THREADS = 5;

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    InventoryRepository inventoryRepository;

    @RepeatedTest(5)
    void whenMultipleConcurrentOrderStatusesAreSuccessThenProductQuantityIsCorrectlyDecreased() throws InterruptedException {
        var orderInputDto = new OrderInputDto();
        orderInputDto.setProductId(1L);
        orderInputDto.setQuantity(1L);

        Runnable createOrderRunnable = () -> restTemplate.postForEntity("http://{host}:{port}/order/create", orderInputDto, OrderOutputDto.class, "localhost", port);

        List<Thread> createOrderThreads = generateThreads(createOrderRunnable, NUMBER_OF_THREADS);
        createOrderThreads.forEach(Thread::start);
        for (Thread thread : createOrderThreads) {
            thread.join();
        }

        List<OrderEntity> orderEntities = orderRepository.findAll();
        assertThat(orderEntities.size()).isEqualTo(5);

        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(1L).get();
        assertThat(inventoryEntity.getQuantity()).isEqualTo(0L);
    }

    private List<Thread> generateThreads(Runnable runnable, int numberOfThreads) {
        return IntStream.range(0, numberOfThreads)
                        .mapToObj(i -> new Thread(runnable))
                        .collect(Collectors.toList());
    }

}