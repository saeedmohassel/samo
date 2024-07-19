package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import com.wallet.walletapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final String USER_ROLE = "User";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    ;

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (isUserExists(userDto.getUsername())) {
            throw new DuplicateKeyException(String.format("username '%s' already exist", userDto.getUsername()));
        }
        AppUser appUser = mapToAppUser(userDto);
        setCredentials(appUser);
        AppUser savedUser = userRepository.save(appUser);
        return mapToUserDto(savedUser);
    }

    private boolean isUserExists(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    private void setCredentials(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setEnabled(true);
        appUser.setRoles(USER_ROLE);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist", username)));
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
