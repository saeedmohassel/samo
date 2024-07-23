package com.wallet.walletapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonRequestDto {

    @NotBlank(message = "")
    private String username;
    @NotBlank(message = "")
    private String firstName;
    @NotBlank(message = "")
    private String lastName;
    @Pattern(regexp = "\\d{10}", message = "")
    private String nationalCode;
    private LocalDate birthdate;
    @NotBlank(message = "")
    private String gender;

}
