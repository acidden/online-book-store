package mate.academy.onlinebookstore.repository;

import java.util.Optional;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = "classpath:database/add-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "classpath:database/clear-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Find cart item by valid ID and shopping cart ID")
    void getCartItemByIdAndShoppingCartId_ValidIds_ReturnsCartItem() {
        Long cartItemId = 1L;
        Long shoppingCartId = 2L;

        Optional<CartItem> actualItem = cartItemRepository
                .getCartItemByIdAndShoppingCartId(cartItemId, shoppingCartId);

        assertTrue(actualItem.isPresent());
        CartItem cartItem = actualItem.get();
        assertEquals(cartItemId, cartItem.getId());
        assertEquals(shoppingCartId, cartItem.getShoppingCart().getId());
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Return empty optional when cart item belongs to another shopping cart")
    void getCartItemByIdAndShoppingCartId_WrongShoppingCartId_ReturnsEmptyOptional() {
        Long cartItemId = 1L;
        Long wrongShoppingCartId = 1L;

        Optional<CartItem> actualItem = cartItemRepository
                .getCartItemByIdAndShoppingCartId(cartItemId, wrongShoppingCartId);
        assertTrue(actualItem.isEmpty());
    }
}
