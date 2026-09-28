package mate.academy.onlinebookstore.repository;

import mate.academy.onlinebookstore.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {
                "classpath:database/add-books-and-categories.sql",
                "classpath:database/add-orders.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:database/clear-orders.sql",
                "classpath:database/clear-books-and-categories.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Find all orders by valid user id with pagination and fetched items")
    void findAllByUserId_ValidUserId_ReturnsOrdersList() {
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Order> actualList = orderRepository.findAllByUserId(userId, pageable);

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        Order order = actualList.get(0);
        assertEquals(userId, order.getUser().getId());
        assertNotNull(order.getOrderItems());
        assertEquals(1, order.getOrderItems().size());
    }

    @Test
    @DisplayName("Find order by valid ID with fetched items")
    void findByIdWithItems_ValidOrderId_ReturnsOrderWithItems() {
        Long orderId = 1L;

        Optional<Order> actual = orderRepository.findByIdWithItems(orderId);

        assertTrue(actual.isPresent());
        Order order = actual.get();
        assertEquals(orderId, order.getId());
        assertNotNull(order.getOrderItems());
        assertEquals(1, order.getOrderItems().size());
    }

    @Test
    @DisplayName("Return empty Optional when order ID does not exist")
    void findByIdWithItems_NonExistingOrderId_ReturnsEmptyOptional() {
        Long nonExistingOrderId = 999L;

        Optional<Order> actual = orderRepository.findByIdWithItems(nonExistingOrderId);

        assertTrue(actual.isEmpty());
    }
}
