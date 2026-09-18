package mate.academy.onlinebookstore.dto;

public record OrderItemResponseDto(
        Long id,
        Long bookId,
        int quantity
) {
}
