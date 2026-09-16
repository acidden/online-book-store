package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.CreateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.ShoppingCartResponseDto;
import mate.academy.onlinebookstore.dto.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management",
        description = "Endpoints for managing user`s shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Retrieve the current authenticated user`s shopping cart")
    public ShoppingCartResponseDto getShoppingCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.getShoppingCart(user.getId());
    }

    @PostMapping
    @Operation(summary = "Add book to cart",
            description = "Add a book to shopping cart or change quantity if already exists")
    public ShoppingCartResponseDto addBookToCart(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateCartItemRequestDto requestDto
    ) {
        return shoppingCartService.addBookToCart(user.getId(), requestDto);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update item quantity",
            description = "Update the quantity of a specific book in the shopping cart")
    public ShoppingCartResponseDto updateCarItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto
    ) {
        return shoppingCartService.updateCartItemQuantity(user.getId(), cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove item from cart",
            description = "Permanently remove a specific book from the shopping cart")
    public void removeCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId
    ) {
        shoppingCartService.removeCartItem(user.getId(), cartItemId);
    }
}
