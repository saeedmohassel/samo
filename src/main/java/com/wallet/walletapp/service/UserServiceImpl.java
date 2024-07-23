package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import com.wallet.walletapp.model.entity.Gender;
import com.wallet.walletapp.model.mapper.UserMapper;
import com.wallet.walletapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final String USER_ROLE = "User";
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (isUserExists(userDto.getUsername())) {
            throw new DuplicateKeyException(String.format("username '%s' already exist", userDto.getUsername()));
        }
        AppUser appUser = userMapper.toEntity(userDto);
        setCredentials(appUser);
        AppUser savedUser = userRepository.save(appUser);
        return userMapper.toDto(savedUser);
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
                .map(userMapper::toDto)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("username '%s' does not exist", username)));
    }

    @Override
    public List<String> getGenders() {
        return Arrays.stream(Gender.values()).map(Gender::name).collect(Collectors.toList());
    }

}