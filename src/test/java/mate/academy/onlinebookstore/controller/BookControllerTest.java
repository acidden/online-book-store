package mate.academy.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import mate.academy.onlinebookstore.BaseIntegrationTest;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(
        scripts = "classpath:database/add-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "classpath:database/clear-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class BookControllerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify createBook() endpoint returns 201 Created and correct JSON")
    void createBook_ValidRequestDto_ReturnsCreated() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Controller Book",
                "Author Name",
                "ISBN-99999",
                BigDecimal.valueOf(49.99),
                "Description",
                "image.jpg",
                java.util.List.of(1L)
        );

        mockMvc.perform(post("/books").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Controller Book")
                );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getAll() endpoint returns list of books")
    void getAll_ValidRequest_ReturnsAllBooks() throws Exception {

        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Title"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getBookById() endpoint returns book when id exists")
    void getBookById_ValidRequest_ReturnsBookDto() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(get("/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify update() endpoint updates book and returns updated BookDto")
    void update_ValidRequest_ReturnsUpdatedBookDto() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Updated Title",
                "Author",
                "ISBN-99999",
                BigDecimal.valueOf(49.99),
                "Description",
                "image.jpg",
                java.util.List.of(1L)
        );

        mockMvc.perform(put("/books/" + bookId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify deleteById() endpoint returns No content status")
    void deleteById_ValidId_returnsNoContentStatus() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete("/books/" + bookId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
