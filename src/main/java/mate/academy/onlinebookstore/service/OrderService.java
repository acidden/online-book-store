package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.OrderItemResponseDto;
import mate.academy.onlinebookstore.dto.OrderResponseDto;
import mate.academy.onlinebookstore.dto.UpdateOrderStatusRequestDto;
import mate.academy.onlinebookstore.model.User;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto);

    List<OrderResponseDto> getOrderHistory(Long userId, Pageable pageable);

    OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    List<OrderItemResponseDto> getOrderItems(Long orderId);

    OrderItemResponseDto getOrderItem(Long orderId, Long itemId);
}
