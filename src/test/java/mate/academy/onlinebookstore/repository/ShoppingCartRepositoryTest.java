package mate.academy.onlinebookstore.repository;

import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find shopping cart by valid user id with items")
    @Sql(
            scripts = "classpath:database/add-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/clear-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findByUserId_ValidUserId_ReturnsShoppingCartWithItems() {
        Long userId = 2L;

        Optional<ShoppingCart> actualCart = shoppingCartRepository.findByUserId(userId);

        assertTrue(actualCart.isPresent());
        ShoppingCart shoppingCart = actualCart.get();
        assertEquals(userId, shoppingCart.getUser().getId());
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    @DisplayName("Returns empty optional when user not exists")
    void findByUserId_NonExistingUserId_ReturnsEmptyOptional() {
        Long nonExistingUserId = 999L;
        Optional<ShoppingCart> actualCart = shoppingCartRepository.findByUserId(nonExistingUserId);

        assertTrue(actualCart.isEmpty());
    }
}
