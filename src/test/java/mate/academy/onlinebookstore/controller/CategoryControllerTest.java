package mate.academy.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.onlinebookstore.BaseIntegrationTest;
import mate.academy.onlinebookstore.dto.CategoryRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

public class CategoryControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify createCategory() endpoint returns 201 Created and correct JSON")
    void createCategory_ValidRequestDto_ReturnsCreated() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("Comedy", "Comedy books");

        mockMvc.perform(post("/categories").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Comedy"))
                .andExpect(jsonPath("$.description").value("Comedy books"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getAllCategories() endpoint returns list of categories")
    void getAllCategories_ValidRequest_ReturnsAllCategories() throws Exception {

        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Fantasy"));

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getCategoryById() endpoint returns correct category when id exists")
    void getCategoryById_ValidId_ReturnsCategoryResponseDto() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(get("/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fantasy"))
                .andExpect(jsonPath("$.description").value("Fantasy books"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify deleteById() endpoint returns No content status")
    void deleteById_ValidId_returnsNoContentStatus() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(delete("/categories/" + categoryId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
