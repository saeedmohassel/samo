package com.wallet.walletapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonRequestDto {

    @NotBlank(message = "{person.username.required}")
    private String username;
    @NotBlank(message = "{person.firstName.required}")
    private String firstName;
    @NotBlank(message = "{person.lastName.required}")
    private String lastName;
    @Pattern(regexp = "\\d{10}", message = "{person.nationalCode.size}")
    private String nationalCode;
    private LocalDate birthdate;
    @NotBlank(message = "{person.gender.required}")
    private String gender;

}
