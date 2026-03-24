package com.MFP.OrderService.repository;

import com.MFP.OrderService.entity.OrderEntity;
import com.MFP.OrderService.enums.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class OrderRepositoryTest {

    @Container
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("orderdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
    @Autowired
    private OrderRepository orderRepository;
    @Test
    @DisplayName("Should save order successfully")
    void shouldSaveOrder(){
        OrderEntity orderEntity =
                OrderEntity.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Created)
                        .build();
        OrderEntity saved = orderRepository.save(orderEntity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(BigDecimal.valueOf(2586));
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.Created);
        assertThat(saved.getUserId()).isEqualTo(1);
    }
    @Test
    @DisplayName("Should find order by id")
    void shouldFindOrderById(){
        OrderEntity orderEntity =
                OrderEntity.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Created)
                        .build();
        OrderEntity saved = orderRepository.save(orderEntity);

        Optional<OrderEntity> byId = orderRepository.findById(saved.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getAmount()).isEqualTo(BigDecimal.valueOf(2586));
    }

    @Test
    @DisplayName("Should be return true when order exits by id")
    void shouldFindOrderByUserId(){
        OrderEntity orderEntity =
                OrderEntity.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Created)
                        .build();
        OrderEntity saved = orderRepository.save(orderEntity);

        boolean exists = orderRepository.existsById(saved.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should be delete order by id")
    void shouldDeleteOrderById(){
        OrderEntity orderEntity =
                OrderEntity.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Created)
                        .build();
        OrderEntity saved = orderRepository.save(orderEntity);

        orderRepository.deleteById(saved.getId());
        Optional<OrderEntity> byId = orderRepository.findById(saved.getId());
        assertThat(byId).isEmpty();


    }
    @Test
    @DisplayName("Should find all the orders")
    void shouldFindAllOrders(){
        OrderEntity orderEntity =
                OrderEntity.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Created)
                        .build();
        OrderEntity orderEntity1 =
                OrderEntity.builder()
                        .userId(2L)
                        .amount(BigDecimal.valueOf(2586))
                        .status(OrderStatus.Approved)
                        .build();
        OrderEntity saved = orderRepository.save(orderEntity);
        OrderEntity saved1 = orderRepository.save(orderEntity1);

        List<OrderEntity> all = orderRepository.findAll();

        assertThat(all.size()).isEqualTo(2);
        assertThat(all.get(0).getStatus()).isEqualTo(OrderStatus.Created);
        assertThat(all.get(1).getStatus()).isEqualTo(OrderStatus.Approved);

    }
}
