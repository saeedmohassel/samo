package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import com.wallet.walletapp.model.mapper.UserMapper;
import com.wallet.walletapp.repository.UserRepository;
import com.wallet.walletapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveUser() {
        String USERNAME = "A1";
        String PASSWORD = "A2";
        String EMAIL = "A3@A3";

        UserDto userDto = new UserDto();
        userDto.setUsername(USERNAME);
        userDto.setPassword(PASSWORD);
        userDto.setEmail(EMAIL);

        AppUser user = new AppUser();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setEnabled(true);
        user.setRoles("User");

        given(passwordEncoder.encode(user.getPassword())).willReturn(PASSWORD);
        given(userRepository.save(any(AppUser.class))).willReturn(user);
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user);
        given(userMapper.toDto(any(AppUser.class))).willReturn(userDto);

        UserDto returnedUser = userService.saveUser(userDto);

        assertThat(returnedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(returnedUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).save(any(AppUser.class));

    }

    @Test
    void findUserByUsername() {
        String USERNAME = "A1";
        String PASSWORD = "A2";
        String EMAIL = "A3@A3";

        UserDto userDto = new UserDto();
        userDto.setUsername(USERNAME);
        userDto.setPassword(PASSWORD);
        userDto.setEmail(EMAIL);

        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setEnabled(true);
        user.setRoles("User");

        given(userRepository.findUserByUsername(USERNAME)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        UserDto returnedUser = userService.findUserByUsername(USERNAME);

        assertThat(returnedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(returnedUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findUserByUsername(USERNAME);
    }
}