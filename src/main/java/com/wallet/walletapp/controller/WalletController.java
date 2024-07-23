package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.WalletService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/register")
    public ResponseEntity<WalletDto> registerWallet(@Valid @RequestBody WalletRequestDto walletRequest) {
        log.info("user register requester: '{}'", walletRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.registerWallet(walletRequest));
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencyList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(walletService.getCurrencyList());
    }

    @GetMapping("/psp")
    public ResponseEntity<List<String>> getPSPList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(walletService.getPSPList());
    }

}
