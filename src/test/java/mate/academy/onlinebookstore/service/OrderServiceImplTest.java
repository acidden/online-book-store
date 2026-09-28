package mate.academy.onlinebookstore.service;

import mate.academy.onlinebookstore.dto.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.OrderItemResponseDto;
import mate.academy.onlinebookstore.dto.OrderResponseDto;
import mate.academy.onlinebookstore.dto.UpdateOrderStatusRequestDto;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.mapper.OrderMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.CartItemRepository;
import mate.academy.onlinebookstore.repository.OrderItemRepository;
import mate.academy.onlinebookstore.repository.OrderRepository;
import mate.academy.onlinebookstore.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private ShoppingCart shoppingCart;
    private Book book;
    private CartItem cartItem;
    private Order order;
    private OrderItem orderItem;
    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        book = new Book();
        book.setId(10L);
        book.setPrice(BigDecimal.valueOf(20));
        cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>(Set.of(cartItem)));
        order = new Order();
        order.setId(1L);
        order.setUser(user);
        orderItem = new OrderItem();
        orderItem.setId(5L);
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setPrice(book.getPrice());
        order.setOrderItems(Set.of(orderItem));
        orderResponseDto = new OrderResponseDto(
                1L,
                1L,
                Set.of(),
                null,
                BigDecimal.valueOf(40),
                "DELIVERED"
        );
    }

    @Test
    @DisplayName("Create order successfully with valid DTO")
    void createOrder_ValidCart_ReturnsOrderDto() {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto("123 Main str");
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto actualDto = orderService.createOrder(user, requestDto);

        assertNotNull(actualDto);
        assertEquals(orderResponseDto, actualDto);
        verify(cartItemRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Create order throws exception when cart is empty")
    void createOrder_EmptyCart_ThrowsIllegalArgumentException() {
        shoppingCart.setCartItems(Collections.emptySet());
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto("123 Main Str");
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(user, requestDto));
    }

    @Test
    @DisplayName("Get order history for user")
    void getOrderHistory_ValidUserId_ReturnsList() {
        Pageable pageable = PageRequest.of(0,10);

        when(orderRepository.findAllByUserId(user.getId(), pageable)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        List<OrderResponseDto> actualList = orderService.getOrderHistory(user.getId(), pageable);

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
    }

    @Test
    @DisplayName("Update order status by admin")
    void updateStatus_ValidId_ReturnsUpdatedOrderDto() {
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto("DELIVERED");

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto actualDto = orderService.updateStatus(order.getId(), requestDto);

        assertNotNull(actualDto);
        assertEquals(Order.Status.DELIVERED, order.getStatus());
    }

    @Test
    @DisplayName("Get order items throws AccessDeniedException for wrong user")
    void getOrderItems_WrongUser_ThrowsAccessDeniedException() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(999L);
        order.setUser(unauthorizedUser);

        when(orderRepository.findByIdWithItems(order.getId())).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class,
                () -> orderService.getOrderItems(user.getId(), order.getId()));
    }

    @Test
    @DisplayName("Get specific order item successfully")
    void getOrderItem_ValidRequest_ReturnsItemDto() {
        when(orderItemRepository.findByIdAndOrderId(orderItem.getId(), order.getId()))
                .thenReturn(Optional.of(orderItem));
        OrderItemResponseDto expectedItemDto = new OrderItemResponseDto(5L, 10L, 2);
        when(orderItemMapper.toDto(orderItem)).thenReturn(expectedItemDto);

        OrderItemResponseDto actual = orderService.getOrderItem(user.getId(), order.getId(), orderItem.getId());

        assertNotNull(actual);
        assertEquals(expectedItemDto, actual);
    }
}
