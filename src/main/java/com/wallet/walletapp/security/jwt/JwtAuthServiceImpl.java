package com.wallet.walletapp.security.jwt;

import com.wallet.walletapp.model.dto.TokenRequest;
import com.wallet.walletapp.model.dto.TokenResponse;
import com.wallet.walletapp.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtAuthServiceImpl implements AuthService {

    private final JwtService jwtService;

    public TokenResponse createToken(TokenRequest request) {
        return createToken(request.getUsername());
    }

    public TokenResponse createToken(String username) {
        return TokenResponse
                .builder()
                .token(jwtService.createToken(username))
                .build();
    }

}
