package com.wallet.walletapp.security;

import com.wallet.walletapp.model.dto.TokenRequest;
import com.wallet.walletapp.model.dto.TokenResponse;

public interface AuthService {

    TokenResponse createToken(TokenRequest request);

    TokenResponse createToken(String username);

}
