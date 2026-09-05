package mate.academy.onlinebookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.onlinebookstore.validation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "incorrect password")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 35)
    private String password;

    @NotBlank
    @Size(min = 8, max = 35)
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String shippingAddress;
}
