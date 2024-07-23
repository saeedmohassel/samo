package com.wallet.walletapp.model.dto;

import com.wallet.walletapp.model.entity.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PersonDto {

    private UserDto user;
    private List<WalletDto> walletList;
    private String firstName;
    private String lastName;
    private String nationalCode;
    private LocalDate birthdate;
    private Gender gender;

}
