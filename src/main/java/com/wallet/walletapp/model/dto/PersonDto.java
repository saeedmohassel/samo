package com.wallet.walletapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wallet.walletapp.model.entity.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
