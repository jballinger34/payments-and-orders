package me.jamie.paymentspractice.dao;


import me.jamie.paymentspractice.dao.order.OracleDbOrderDao;
import me.jamie.paymentspractice.data.entity.OrderEntity;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.exception.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OracleDbOrderDaoTest {

    @Autowired
    OracleDbOrderDao dao;

    @Test
    void testSaveAndFindByMerchantIdAndId() throws Exception {

        OrderEntity orderEntity = new OrderEntity(
                "ORDER_1",
                "TEST_MERCHANT",
                OrderStatus.CREATED,
                "PAYMENT_1"
        );
        orderEntity.setItems(new ArrayList<>());
        dao.save(orderEntity);

        OrderEntity found = dao.findByMerchantIdAndId("TEST_MERCHANT","ORDER_1");

        assertNotNull(found);

        assertEquals(orderEntity.getId(), found.getId());
        assertEquals(orderEntity.getMerchantId(), found.getMerchantId());

        assertNotNull(found.getItems());
        assertEquals(0, found.getItems().size());

        assertEquals(orderEntity.getStatus(), found.getStatus());
    }

    @Test
    void testFindByMerchantId() throws Exception {

        OrderEntity order1 = new OrderEntity(
                "ORDER_1",
                "TEST_MERCHANT",
                OrderStatus.CREATED,
                "PAYMENT_1"
        );

        OrderEntity order2 = new OrderEntity(
                "ORDER_2",
                "TEST_MERCHANT",
                OrderStatus.RESERVED,
                "PAYMENT_2"
        );

        dao.save(order1);
        dao.save(order2);

        List<OrderEntity> orders = dao.findByMerchantId("TEST_MERCHANT");

        assertNotNull(orders);

        assertTrue(orders.stream().anyMatch(entity -> entity.getId().equals("ORDER_1")));

        assertTrue(orders.stream().anyMatch(entity -> entity.getId().equals("ORDER_2")));
    }

    @Test
    void testGetNotFoundOrder() {
        assertThrows(OrderNotFoundException.class, () -> dao.findByMerchantIdAndId("TEST_MERCHANT","DOES_NOT_EXIST"));
    }
    @Test
    void testMerchantNotFound() throws Exception {
        List<OrderEntity> shouldBeEmpty = dao.findByMerchantId("NOT_FOUND_MERCHANT");
        assertNotNull(shouldBeEmpty);
        assertEquals(0, shouldBeEmpty.size());
    }
}
