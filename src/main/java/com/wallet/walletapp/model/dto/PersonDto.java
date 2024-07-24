package com.wallet.walletapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wallet.walletapp.model.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(
        description = "PersonDto Model Information"
)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDto {

    private UserDto user;
    private List<WalletDto> walletList;
    private String firstName;
    private String lastName;
    private String nationalCode;
    private LocalDate birthdate;
    private Gender gender;

}
