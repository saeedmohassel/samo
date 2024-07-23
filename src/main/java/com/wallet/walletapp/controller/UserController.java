package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.*;
import com.wallet.walletapp.security.AuthService;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.PersonService;
import com.wallet.walletapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Tag(
        name = "User Controller",
        description = "Authentication API - signup, login, find user"
)
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final PersonService personService;

    @Operation(
            summary = "Register User",
            description = "Register new User and returns an access token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User Registered Successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "User Registration Failed")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@Valid @RequestBody UserDto user) {
        log.info("user register requester: '{}'", user.getUsername());
        UserDto newUser = userService.saveUser(user);
        TokenResponse tokenResponse = authService.createToken(newUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @Operation(
            summary = "Find User",
            description = "Find User With Username"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User Found successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))}),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{username}")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("user info requester: '{}'", username);
        UserDto user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "User Login",
            description = "Authenticates the user and returns an access token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> getLoginToken(@RequestBody TokenRequest request) {
        log.info("login token requester: '{}'", request.getUsername());
        TokenResponse tokenResponse = authService.authenticateAndGetToken(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/{username}/profile")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<PersonDto> registerProfile(@Valid @RequestBody PersonRequestDto personRequest,
                                                     @PathVariable String username) {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        PersonDto personDto = personService.registerPerson(personRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(personDto);
    }

    @GetMapping("/genders")
    public ResponseEntity<List<String>> getGenderList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userService.getGenders());
    }


}
