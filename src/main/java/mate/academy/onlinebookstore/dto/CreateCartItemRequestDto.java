package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @NotNull(message = "Book ID cannot be null")
        Long bookId,
        @Positive(message = "Quantity must be greater than 0")
        int quantity
) {
}
