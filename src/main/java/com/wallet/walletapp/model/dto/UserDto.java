package com.wallet.walletapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "{user.username.required}")
    @Size(min = 2, max = 20, message = "{user.username.size.not.valid}")
    private String username;

    @NotBlank(message = "{user.password.required}")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.not.valid}")
    private String email;
}