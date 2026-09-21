package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.OrderItemResponseDto;
import mate.academy.onlinebookstore.dto.OrderResponseDto;
import mate.academy.onlinebookstore.dto.UpdateOrderStatusRequestDto;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Management", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place a new order",
            description = "Creates an order based on current shopping cart items")
    public OrderResponseDto createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateOrderRequestDto requestDto
    ) {
        return orderService.createOrder(user, requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order history",
            description = "Retrieves user`s order history")
    public List<OrderResponseDto> getOrderHistory(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status",
            description = "Allows administrators update an order status")
    public OrderResponseDto updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequestDto requestDto
    ) {
        return orderService.updateStatus(id, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all order items",
            description = "Retrieves all items for a specific order")
    public List<OrderItemResponseDto> getOrderItems(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId
    ) {
        return orderService.getOrderItems(user.getId(), orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get a specific order item",
            description = "Retrieves a specific item within an order")
    public OrderItemResponseDto getOrderItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return orderService.getOrderItem(user.getId(), orderId, itemId);
    }
}
