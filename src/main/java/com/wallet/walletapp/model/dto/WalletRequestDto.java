package com.wallet.walletapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(
        description = "WalletRequestDto Model Information"
)
@Data
public class WalletRequestDto {


    @NotBlank(message = "{wallet.username.required}")
    private String username;

    @NotBlank(message = "{wallet.walletName.required}")
    @Size(min = 5, max = 20, message = "{wallet.walletName.size.not.valid}")
    private String walletName;

    @NotBlank(message = "{wallet.currencyCode.required}")
    @Size(min = 3, max = 3, message = "{wallet.currencyCode.size.not.valid}")
    private String currencyCode;

}
