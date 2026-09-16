package mate.academy.onlinebookstore.dto;

import java.util.Set;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        Set<CartItemResponseDto> cartItems
) {
}
