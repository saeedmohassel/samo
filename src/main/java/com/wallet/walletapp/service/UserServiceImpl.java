package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import com.wallet.walletapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        AppUser appUser = mapToAppUser(userDto);
        AppUser savedUser = userRepository.save(appUser);
        return mapToUserDto(savedUser);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new RuntimeException(String.format("username '%s' does not exist", username)));
    }

    private AppUser mapToAppUser(UserDto userDto) {
        AppUser appUser = new AppUser();
        appUser.setUsername(userDto.getUsername());
        appUser.setPassword(userDto.getPassword());
        appUser.setEmail(userDto.getEmail());
        return appUser;
    }

    private UserDto mapToUserDto(AppUser appUser) {
        UserDto userDto = new UserDto();
        userDto.setUsername(appUser.getUsername());
        userDto.setPassword(appUser.getPassword());
        userDto.setEmail(appUser.getEmail());
        return userDto;
    }
}
