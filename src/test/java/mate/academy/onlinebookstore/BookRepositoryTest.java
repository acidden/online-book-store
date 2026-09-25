package mate.academy.onlinebookstore;

import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Verify findAllByCategoriesId() returns book matching the category id")
    @Sql(scripts = "classpath:add-books-and-categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:clear-books-and-categories.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_ValidCategoryId_ReturnsMatchingBooks() {
        Long categoryId =1L;

        List<Book> actualBooks = bookRepository.findAllByCategoriesId(categoryId);

        assertFalse(actualBooks.isEmpty(), "Books list should not be empty");
        assertEquals(1, actualBooks.size());
        assertEquals("Title", actualBooks.get(0).getTitle());
    }
}
