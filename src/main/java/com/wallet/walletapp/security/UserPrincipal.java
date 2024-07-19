package com.wallet.walletapp.security;

import com.wallet.walletapp.model.entity.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final AppUser user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoles() != null) {
            authorities = Arrays.stream(StringUtils.tokenizeToStringArray(user.getRoles(), ","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }


}
