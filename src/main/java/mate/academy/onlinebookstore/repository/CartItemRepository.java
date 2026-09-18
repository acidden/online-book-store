package mate.academy.onlinebookstore.repository;

import java.util.Optional;
import mate.academy.onlinebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> getCartItemByIdAndShoppingCartId(Long id, Long shoppingCartId);
}
