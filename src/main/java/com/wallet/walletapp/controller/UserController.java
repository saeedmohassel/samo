package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.TokenRequest;
import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.security.AuthService;
import com.wallet.walletapp.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto user) {
        UserDto newUser = userService.saveUser(user);
        return new ResponseEntity<>(String.format("user '%s' created successfully!", newUser.getUsername()), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> getLoginToken(@RequestBody TokenRequest request) {
        return new ResponseEntity<>(authService.createToken(request.getUsername()), HttpStatus.OK);
    }

}
