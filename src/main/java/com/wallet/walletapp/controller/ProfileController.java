package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.PersonDto;
import com.wallet.walletapp.model.dto.PersonRequestDto;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.PersonService;
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
@RequestMapping("/profile")
public class ProfileController {

    private final PersonService personService;

    @GetMapping("/genders")
    public ResponseEntity<List<String>> getGenderList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(personService.getGenders());
    }

    @PostMapping("/{username}")
    @PreAuthorize("principal.username == #username")
    public ResponseEntity<PersonDto> registerProfile(@Valid @RequestBody PersonRequestDto personRequest,
                                                     @PathVariable String username) {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        PersonDto personDto = personService.registerPerson(personRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(personDto);
    }

}
