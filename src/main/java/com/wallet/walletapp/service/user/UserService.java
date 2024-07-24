package com.wallet.walletapp.service.user;

import com.wallet.walletapp.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto findUserByUsername(String username);

    List<String> getGenders();

}
