package mate.academy.onlinebookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import mate.academy.onlinebookstore.controller.ShoppingCartController;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@WebMvcTest(ShoppingCartController.class)
public class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private ShoppingCartService shoppingCartService;

    private User customUser;
    private ShoppingCartResponseDto responseDto;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setName(Role.RoleName.ROLE_USER);

        customUser = new User();
        customUser.setId(1L);
        customUser.setEmail("user@example.com");
        customUser.setPassword("password");
        customUser.setRoles(Set.of(userRole));

        responseDto = new ShoppingCartResponseDto(1L, 1L, Collections.emptySet());

        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUser);
    }

    @Test
    @DisplayName("Verify getShoppingCart() endpoint returns correct DTO")
    void getShoppingCart_ValidUser_ReturnsShoppingCart() throws Exception {
        when(shoppingCartService.getShoppingCart(customUser.getId())).thenReturn(responseDto);

        mockMvc.perform(get("/cart")
                        .with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customUser.getId()))
                .andExpect(jsonPath("$.userId").value(responseDto.userId()));
        verify(shoppingCartService).getShoppingCart(customUser.getId());
    }

    @Test
    @DisplayName("Verify addBookToCart() adds book and returns updated cart")
    void addBookToCart_ValidRequest_ReturnsShoppingCart() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(5L, 2);

        when(shoppingCartService.addBookToCart(eq(customUser.getId()),
                any(CreateCartItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/cart")
                        .with(user(customUser)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customUser.getId()));

        verify(shoppingCartService).addBookToCart(eq(customUser.getId()),
                any(CreateCartItemRequestDto.class));
    }

    @Test
    @DisplayName("Verify updateCartItemQuantity() updates quantity and returns cart")
    void updateCartItemQuantity_ValidRequest_ReturnsShoppingCart() throws Exception {
        Long cartItemId = 10L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);

        when(shoppingCartService.updateCartItemQuantity(eq(customUser.getId()), eq(cartItemId),
                any(UpdateCartItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/cart/items/" + cartItemId)
                        .with(user(customUser)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customUser.getId()));

        verify(shoppingCartService).updateCartItemQuantity(eq(customUser.getId()), eq(cartItemId),
                any(UpdateCartItemRequestDto.class));
    }

    @Test
    @DisplayName("Verify removeCartItem() deletes item and returns No Content status")
    void removeCartItem_ValidRequest_ReturnsNoContent() throws Exception {
        Long cartItemId = 10L;

        mockMvc.perform(delete("/cart/items/" + cartItemId)
                        .with(user(customUser)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(shoppingCartService).removeCartItem(customUser.getId(), cartItemId);
    }
}
