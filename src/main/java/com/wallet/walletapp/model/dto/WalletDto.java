package com.wallet.walletapp.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletDto {

    private String name;
    private Long address;
    private BigDecimal balance;
    private String currencyCode;
    private LocalDateTime registerDate;

}
