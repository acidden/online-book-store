package mate.academy.onlinebookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.OrderItemResponseDto;
import mate.academy.onlinebookstore.dto.OrderResponseDto;
import mate.academy.onlinebookstore.dto.UpdateOrderStatusRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.mapper.OrderMapper;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.CartItemRepository;
import mate.academy.onlinebookstore.repository.OrderItemRepository;
import mate.academy.onlinebookstore.repository.OrderRepository;
import mate.academy.onlinebookstore.repository.ShoppingCartRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Can`t find shopping cart for user id: "
                                + user.getId()));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty. Cannot create an order.");
        }
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.shippingAddress());

        BigDecimal total = BigDecimal.ZERO;
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    return orderItem;
                })
                .collect(Collectors.toSet());
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        shoppingCart.getCartItems().clear();

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public List<OrderResponseDto> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find order by id: "
                        + orderId));
        order.setStatus(Order.Status.valueOf(requestDto.status()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemResponseDto> getOrderItems(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can`t find order by id: "
                                + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You don`t have permission to view items of this order");
        }
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItem(Long userId, Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find order item by id: "
                        + itemId + "for order id: " + orderId));
        if (!orderItem.getOrder().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to view this order item");
        }
        return orderItemMapper.toDto(orderItem);
    }
}
