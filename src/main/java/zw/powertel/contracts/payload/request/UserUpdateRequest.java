package zw.powertel.contracts.payload.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.powertel.contracts.enums.Role;
import zw.powertel.contracts.validation.StrongPassword;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "firstname is required")
    private String firstname;

    @NotBlank(message = "lastname is required")
    private String lastname;

    @Column(unique=true)
    @NotBlank(message = "email is required")
    @Email(message = "email format is not valid")
    private String email;

    @NotBlank(message = "password is required")
    @StrongPassword
    private String password;

    @NotNull
    private Role role;

    @NotBlank(message = "phone is required")
    private String phone;

}
