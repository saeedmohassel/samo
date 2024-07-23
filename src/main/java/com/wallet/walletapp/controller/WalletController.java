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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/register")
    @PreAuthorize("principal.username == #walletRequest.username")
    public ResponseEntity<WalletDto> registerWallet(@Valid @RequestBody WalletRequestDto walletRequest) {
        log.info("register wallet requester: '{}'", walletRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.registerWallet(walletRequest));
    }

    @GetMapping("/{walletAddress}")
    @PostAuthorize("principal.username == returnObject.body.username")
    public ResponseEntity<WalletDto> findWalletByWalletAddress(@PathVariable Long walletAddress) {
        log.info("wallet info requester: '{}' resource address: '{}'",
                ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getUsername(), walletAddress);
        return ResponseEntity.ok(walletService.findWalletByAddress(walletAddress));
    }

    @PostMapping("/{walletAddress}/deposit")
    public ResponseEntity<WalletDto> deposit(@PathVariable Long walletAddress,
                                             @RequestParam BigDecimal amount,
                                             @RequestParam String pspCode) {
        return ResponseEntity.ok(walletService.deposit(walletAddress, amount, pspCode));
    }

    @PostMapping("/{walletAddress}/withdraw")
    public ResponseEntity<WalletDto> withdraw(@PathVariable Long walletAddress,
                                              @RequestParam BigDecimal amount,
                                              @RequestParam String pspCode) {
        return ResponseEntity.ok(walletService.withdraw(walletAddress, amount, pspCode));
    }

    @PostMapping("/{walletAddress}/transfer/{toWalletId}")
    public ResponseEntity<String> transfer(@PathVariable Long walletAddress,
                                           @PathVariable Long toWalletId,
                                           @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.transfer(walletAddress, toWalletId, amount));
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencyList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.ok(walletService.getCurrencyList());
    }

    @GetMapping("/psp")
    public ResponseEntity<List<String>> getPSPList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.ok(walletService.getPSPList());
    }

}
