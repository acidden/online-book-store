package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @Positive
        Long bookId,
        @Positive(message = "Quantity must be greater than 0")
        int quantity
) {
}
