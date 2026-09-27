package mate.academy.onlinebookstore.service;

import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getShoppingCart(Long userId);

    ShoppingCartResponseDto addBookToCart(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartResponseDto updateCartItemQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto
    );

    ShoppingCartResponseDto removeCartItem(Long userId, Long cartItemId);

    void registerNewShoppingCart(User user);
}
