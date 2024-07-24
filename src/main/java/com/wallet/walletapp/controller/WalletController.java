package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(
        name = "Wallet Controller",
        description = "Wallet API - create, find, deposit, withdraw, transfer, currency list,  PSP list"
)
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Register Wallet",
            description = "register wallet for user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "WalletRequestDto object for creating a wallet",
                    content = @Content(schema = @Schema(implementation = WalletRequestDto.class))
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Wallet Created Successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters"),
                    @ApiResponse(responseCode = "404", description = "User Not Found")
            }
    )
    @PostMapping("/register")
    @PreAuthorize("principal.username == #walletRequest.username")
    public ResponseEntity<WalletDto> registerWallet(@Valid @RequestBody WalletRequestDto walletRequest) {
        log.info("register wallet requester: '{}'", walletRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.registerWallet(walletRequest));
    }

    @Operation(
            summary = "Find Wallet",
            description = "find wallet by wallet address"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Wallet loaded successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Wallet Not Found")
            }
    )
    @GetMapping("/{walletAddress}")
    @PostAuthorize("principal.username == returnObject.body.username")
    public ResponseEntity<WalletDto> findWalletByWalletAddress(
            @Parameter(description = "Wallet Address of the user to be retrieved", required = true)
            @PathVariable Long walletAddress) {
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
