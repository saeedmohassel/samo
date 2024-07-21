package com.wallet.walletapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(
        description = "Required Fields To Get Token "
)
@Setter
@Getter
public class TokenRequest {

    private String username;
    private String password;

}
