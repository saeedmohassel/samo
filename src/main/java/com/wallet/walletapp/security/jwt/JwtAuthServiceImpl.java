package com.wallet.walletapp.security.jwt;

import com.wallet.walletapp.model.dto.TokenRequest;
import com.wallet.walletapp.model.dto.TokenResponse;
import com.wallet.walletapp.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtAuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public TokenResponse authenticateAndGetToken(TokenRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        return createToken(request.getUsername());
    }

    public TokenResponse createToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        return TokenResponse
                .builder()
                .token(jwtService.createToken(user))
                .build();
    }

}
