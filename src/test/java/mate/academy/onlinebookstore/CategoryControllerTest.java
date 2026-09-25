package mate.academy.onlinebookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.onlinebookstore.controller.CategoryController;
import mate.academy.onlinebookstore.dto.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.CategoryResponseDto;
import mate.academy.onlinebookstore.security.JwtUtil;
import mate.academy.onlinebookstore.service.BookService;
import mate.academy.onlinebookstore.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private BookService bookService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify createCategory() endpoint returns 201 Created and correct JSON")
    void createCategory_ValidRequestDto_ReturnsCreated() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "Fantasy",
                "Fantasy Fiction"
        );
        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Fantasy",
                "Fantasy Fiction"
        );

        when(categoryService.save(any())).thenReturn(responseDto);

        mockMvc.perform(post("/categories").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fantasy"))
                .andExpect(jsonPath("$.description").value("Fantasy Fiction"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getAllCategories() endpoint returns list of categories")
    void getAllCategories_ValidRequest_ReturnsAllCategories() throws Exception {
        CategoryResponseDto category1 = new CategoryResponseDto(
                1L,
                "Fantasy",
                "Fantasy Fiction"
        );
        CategoryResponseDto category2 = new CategoryResponseDto(
                2L,
                "Sci-Fi",
                "Science Fiction"
        );

        List<CategoryResponseDto> expectedCategories = List.of(category1, category2);

        when(categoryService.findAll()).thenReturn(expectedCategories);

        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Fantasy"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Sci-Fi"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getCategoryById() endpoint returns correct category when id exists")
    void getCategoryById_ValidId_ReturnsCategoryResponseDto() throws Exception {
        Long categoryId = 1L;
        CategoryResponseDto responseDto = new CategoryResponseDto(
                categoryId,
                "Fantasy",
                "Fantasy Fiction"
        );

        when(categoryService.findById(categoryId)).thenReturn(responseDto);

        mockMvc.perform(get("/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fantasy"))
                .andExpect(jsonPath("$.description").value("Fantasy Fiction"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify deleteById() endpoint returns No content status")
    void deleteById_ValidId_returnsNoContentStatus() throws Exception {
        Long categoryId = 1L;

        doNothing().when(categoryService).deleteById(categoryId);

        mockMvc.perform(delete("/categories/" + categoryId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
