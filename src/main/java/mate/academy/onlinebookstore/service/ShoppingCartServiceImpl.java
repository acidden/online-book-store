package mate.academy.onlinebookstore.service;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartItemMapper;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.BookRepository;
import mate.academy.onlinebookstore.repository.CartItemRepository;
import mate.academy.onlinebookstore.repository.ShoppingCartRepository;
import mate.academy.onlinebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartResponseDto getShoppingCart(Long userId) {
        return shoppingCartMapper.toDto(getOrCreateCart(userId));
    }

    @Override
    public ShoppingCartResponseDto addBookToCart(Long userId, CreateCartItemRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId));
        ShoppingCart shoppingCart = getOrCreateCart(userId);

        CartItem existingItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.bookId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + requestDto.quantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(requestDto.quantity());
            Book book = bookRepository.findById(requestDto.bookId())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("Book not found with id: "
                                            + requestDto.bookId()));
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
            cartItemRepository.save(cartItem);
            shoppingCart.getCartItems().add(cartItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto updateCartItemQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto
    ) {
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository
                .getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can`t find cart item with id: "
                        + cartItemId + " for this user"));
        cartItem.setQuantity(requestDto.quantity());
        return shoppingCartMapper.toDto(getOrCreateCart(userId));
    }

    @Override
    public ShoppingCartResponseDto removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository
                .getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item with id: " + cartItemId + " for this user"
                ));

        cartItemRepository.delete(cartItem);

        return shoppingCartMapper.toDto(getOrCreateCart(userId));
    }

    private ShoppingCart getOrCreateCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow(
                            () -> new EntityNotFoundException("User not found with id:" + userId));
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });
    }
}
