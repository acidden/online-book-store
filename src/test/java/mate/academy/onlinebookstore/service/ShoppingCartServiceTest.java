package mate.academy.onlinebookstore.service;

import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.BookRepository;
import mate.academy.onlinebookstore.repository.CartItemRepository;
import mate.academy.onlinebookstore.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private User user;
    private Book book;
    private ShoppingCart shoppingCart;
    private CartItem cartItem;
    private ShoppingCartResponseDto expectedResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        book = new Book();
        book.setId(10L);
        book.setTitle("Sample Book");
        cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        expectedResponseDto = new ShoppingCartResponseDto(1L, 1L, Set.of());
    }

    @Test
    @DisplayName("Get shopping cart by valid user ID")
    void getShoppingCart_ValidUserId_ReturnsShoppingCartDto() {
        Long userId = user.getId();
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.getShoppingCart(userId);

        assertNotNull(actualDto);
        assertEquals(expectedResponseDto, actualDto);
        verify(shoppingCartRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Add completely new book to cart")
    void addBookToCart_NewBook_SuccessfullyCreatesCartItem() {
        Long userId = user.getId();
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(book.getId(), 3);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.addBookToCart(userId, requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedResponseDto, actualDto);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Add book that already exists quantity to cart increases quantity")
    void addBookToCart_ExistingBook_IncreasesQuantity() {
        Long userId = user.getId();
        shoppingCart.getCartItems().add(cartItem);
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(book.getId(), 3);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        shoppingCartService.addBookToCart(userId, requestDto);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Add book throws exception when book is not found")
    void addBookToCart_NonExistingBook_ThrowsEntityNotFoundException() {
        Long userId = user.getId();
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(999L, 1);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(userId, requestDto));
    }

    @Test
    @DisplayName("Remove item from cart successfully")
    void removeCartItem_ValidRequest_RemovesItemFromCart() {
        Long userId = user.getId();
        Long cartItemId = cartItem.getId();
        shoppingCart.getCartItems().add(cartItem);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedResponseDto);

        ShoppingCartResponseDto actualDto = shoppingCartService.removeCartItem(userId, cartItemId);

        assertNotNull(actualDto);
        assertEquals(0, shoppingCart.getCartItems().size());
        verify(cartItemRepository).delete(cartItem);
    }
}
