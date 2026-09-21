package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequestDto(
        @NotBlank(message = "Status cannot be blank")
        String status
) {
}
