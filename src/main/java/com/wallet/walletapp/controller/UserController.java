package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.*;
import com.wallet.walletapp.security.AuthService;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.PersonService;
import com.wallet.walletapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
            description = "Register new User and returns an access token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UserDto object for creating a user",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            )
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
    public ResponseEntity<UserDto> getUserByUsername(
            @Parameter(description = "username of the user to be retrieved", required = true)
            @PathVariable String username) {
        log.info("user info requester: '{}'", username);
        UserDto user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "User Login",
            description = "Authenticates the user and returns an access token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "TokenRequest object for creating a token",
                    content = @Content(schema = @Schema(implementation = TokenRequest.class))
            )
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

    @Operation(
            summary = "Register Profile",
            description = "Users should create a profile to work with the application",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Person object for creating a profile",
                    content = @Content(schema = @Schema(implementation = PersonRequestDto.class))
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Profile created successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PersonDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User Not Found")
            }
    )
    @PostMapping("/{username}/profile")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<PersonDto> registerProfile(@Valid @RequestBody PersonRequestDto personRequest,
                                                     @Parameter(description = "Username of the profile to be created", required = true)
                                                     @PathVariable String username) {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        PersonDto personDto = personService.registerPerson(personRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(personDto);
    }

    @Operation(
            summary = "Get User Profile",
            description = "load user profile information"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Profile loaded successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User Not Found")
            }
    )
    @GetMapping("/{username}/profile")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<PersonDto> getProfile(
            @Parameter(description = "username of the profile to be retrieved", required = true)
            @PathVariable String username) {
        log.info("user profile requester: '{}' resource address: '{}'",
                ((UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername(), username);
        PersonDto personDto = personService.loadPersonByUsername(username);
        return ResponseEntity.ok(personDto);
    }

    @Operation(
            summary = "Get Gender List",
            description = "load gender list of persons"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "gender list loaded successfully",
                            content = {
                                    @Content(mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
                            })
            }
    )
    @GetMapping("/genders")
    public ResponseEntity<List<String>> getGenderList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userService.getGenders());
    }

}
