package mate.academy.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;

import mate.academy.onlinebookstore.BaseIntegrationTest;
import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.security.JwtUtil;
import mate.academy.onlinebookstore.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(
        scripts = "classpath:database/add-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "classpath:database/clear-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@WithUserDetails("user@example.com")
public class ShoppingCartControllerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Verify getShoppingCart() endpoint returns correct DTO")
    void getShoppingCart_ValidUser_ReturnsShoppingCart() throws Exception {

        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    @DisplayName("Verify addBookToCart() adds book and returns updated cart")
    void addBookToCart_ValidRequest_ReturnsShoppingCart() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(1L, 2);

        mockMvc.perform(post("/cart").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Verify updateCartItemQuantity() updates quantity and returns cart")
    void updateCartItemQuantity_ValidRequest_ReturnsShoppingCart() throws Exception {
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);

        mockMvc.perform(put("/cart/items/" + cartItemId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Verify removeCartItem() deletes item and returns No Content status")
    void removeCartItem_ValidRequest_ReturnsNoContent() throws Exception {
        Long cartItemId = 1L;

        mockMvc.perform(delete("/cart/items/" + cartItemId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
