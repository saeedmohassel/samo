package com.wallet.walletapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(
        description = "The Response That Contains Token"
)
@Getter
@Builder
public class TokenResponse {

    private String token;

}