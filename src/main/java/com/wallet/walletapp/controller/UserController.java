package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserDto user) {
        UserDto newUser = userService.saveUser(user);
        return new ResponseEntity<>(String.format("user '%s' created successfully!", newUser.getUsername()), HttpStatus.CREATED);
    }

}
