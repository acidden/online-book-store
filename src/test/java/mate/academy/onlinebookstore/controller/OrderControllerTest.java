package mate.academy.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.onlinebookstore.dto.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.UpdateOrderStatusRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@WithUserDetails("user@example.com")

public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Verify createOrder() endpoint places order successfully")
    @Sql(
            scripts = "classpath:database/add-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            // Сначала удаляем позиции и заказы, а только потом книги и категории!
            scripts = {
                    "classpath:database/clear-orders.sql",
                    "classpath:database/clear-books-and-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createOrder_ValidCart_ReturnsCreatedOrder() throws Exception {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto("123 Green str");

        mockMvc.perform(post("/orders").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Verify getOrderHistory() endpoint returns correct order list")
    @Sql(
            scripts = {
                    "classpath:database/add-books-and-categories.sql",
                    "classpath:database/add-orders.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/clear-orders.sql",
                    "classpath:database/clear-books-and-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getOrderHistory_ValidUser_ReturnsHistoryList() throws Exception {
        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Verify updateStatus() endpoint updates order status successfully by admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = {
                    "classpath:database/add-books-and-categories.sql",
                    "classpath:database/add-orders.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/clear-orders.sql",
                    "classpath:database/clear-books-and-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateStatus_AsAdmin_ReturnsUpdatedOrder() throws Exception {
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto("DELIVERED");

        mockMvc.perform(patch("/orders/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @DisplayName("Verify getOrderItems() endpoint returns items for a specific order")
    @Sql(
            scripts = {
                    "classpath:database/add-books-and-categories.sql",
                    "classpath:database/add-orders.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/clear-orders.sql",
                    "classpath:database/clear-books-and-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getOrderItems_ValidRequest_ReturnsItemsList() throws Exception {
        mockMvc.perform(get("/orders/1/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Verify getOrderItem() endpoint returns a single specific item")
    @Sql(
            scripts = {
                    "classpath:database/add-books-and-categories.sql",
                    "classpath:database/add-orders.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/clear-orders.sql",
                    "classpath:database/clear-books-and-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getOrderItem_ValidRequest_ReturnsItemDetails() throws Exception {
        mockMvc.perform(get("/orders/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(2));
    }
}
