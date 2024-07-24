package com.wallet.walletapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wallet.walletapp.model.entity.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(
        description = "WalletDto Model Information"
)
@Data
public class WalletDto {

    private String username;
    private String walletName;
    private Long walletAddress;
    private BigDecimal balance;
    private Currency currencyCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDate;

}
