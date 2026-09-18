package mate.academy.onlinebookstore.repository;

import java.util.Optional;
import mate.academy.onlinebookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi "
            + "LEFT JOIN FETCH oi.order o "
            + "LEFT JOIN FETCH o.user "
            + "WHERE oi.id = :itemId AND o.id = :orderId"
    )
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);
}
