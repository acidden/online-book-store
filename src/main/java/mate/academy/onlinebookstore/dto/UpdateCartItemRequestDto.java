package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @Positive(message = "Quantity must be greater than 0")
        int quantity
) {
}
