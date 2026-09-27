package mate.academy.onlinebookstore.service;

import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartItemMapper;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.BookRepository;
import mate.academy.onlinebookstore.repository.CartItemRepository;
import mate.academy.onlinebookstore.repository.ShoppingCartRepository;
import mate.academy.onlinebookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private User user;
    private ShoppingCart shoppingCart;
    private ShoppingCartResponseDto expectedResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(10L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        expectedResponseDto = new ShoppingCartResponseDto(
                10L,
                1L,
                new HashSet<>()
        );
    }

    @Test
    @DisplayName("Verify getShoppingCart() returns existing cart DTO")
    void getShoppingCart_ExistingCart_ReturnsShoppingCartDto() {
        Long userId = user.getId();

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.getShoppingCart(userId);

        assertNotNull(actualDto);
        assertEquals(expectedResponseDto.id(), actualDto.id());
        assertEquals(expectedResponseDto.userId(), actualDto.userId());
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Verify getShoppingCart() creates new cart if not found")
    void getShoppingCart_NewCart_CreatesAndReturnsCartDto() {
        Long userId = user.getId();

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.getShoppingCart(userId);

        assertNotNull(actualDto);
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("Verify updateCartItemQuantity() modifies quantity successfully")
    void updateCartItemQuantity_ValidRequest_ReturnsModifiedQuantity() {
        Long userId = user.getId();
        Long cartItemId = 10L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(3);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService
                .updateCartItemQuantity(userId, cartItemId, requestDto);

        assertNotNull(actualDto);
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository, times(1))
                .getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
    }

    @Test
    @DisplayName("Verify addBookToCart() updates quantity if book already exists")
    void addBookToCart_ExistingItem_UpdatesQuantity() {
        Long userId = user.getId();
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                5L,
                3
        );
        Book book = new Book();
        book.setId(5L);

        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(2);
        shoppingCart.getCartItems().add(existingItem);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.addBookToCart(userId, requestDto);

        assertNotNull(actualDto);
        assertEquals(5, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    @DisplayName("Verify addBookToCart() creates new item if it does not exist")
    void addBookToCart_NewItem_SavesCartItem() {
        Long userId = user.getId();
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                5L,
                3
        );
        Book book = new Book();
        book.setId(5L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.addBookToCart(userId, requestDto);

        assertNotNull(actualDto);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    @DisplayName("Verify removeCartItem() deletes item from repository")
    void removeCartItem_ValidRequest_DeletesCartItem() {
        Long userId = user.getId();
        Long cartItemId = 10L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.removeCartItem(userId, cartItemId);

        assertNotNull(actualDto);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("Verify Exception when User not found in getOrCreateCart")
    void getOrCreateCart_UserNotFound_ThrowsEntityNotFoundException() {
        Long userId = 999L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.getShoppingCart(userId));
    }

    @Test
    @DisplayName("Verify Exception when Cart Item not found for update")
    void updateCartItemQuantity_ItemNotFound_ThrowsEntityNotFoundException() {
        Long userId = user.getId();
        Long cartItemId = 999L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(userId, cartItemId, requestDto));
    }
}
