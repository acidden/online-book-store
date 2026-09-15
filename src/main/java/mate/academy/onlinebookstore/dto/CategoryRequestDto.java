package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank(message = "Name cannot be empty")
        String name,
        String description
) {
}
