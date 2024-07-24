package com.wallet.walletapp.controller;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;
import com.wallet.walletapp.security.UserPrincipal;
import com.wallet.walletapp.service.wallet.WalletService;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                    description = "WalletRequestDto object for creating a wallet." +
                            "CurrencyCode should match one of the values returned by the `Get Currency List` API.",
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

    @Operation(
            summary = "Deposit Money",
            description = "deposit money to wallet"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "deposit successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Wallet Not Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters")
            }
    )
    @PostMapping("/{walletAddress}/deposit")
    public ResponseEntity<WalletDto> deposit(
            @Parameter(description = "Wallet Address for deposit", required = true)
            @PathVariable Long walletAddress,
            @Parameter(description = "amount to be deposited", required = true)
            @RequestParam BigDecimal amount,
            @Parameter(description = "PSP code for transaction, Should match one of the values returned by the `Get PSP List` API.", required = true)
            @RequestParam String pspCode) {
        return ResponseEntity.ok(walletService.deposit(walletAddress, amount, pspCode));
    }

    @Operation(
            summary = "Withdraw Money",
            description = "withdraw money from wallet"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "withdraw successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Wallet Not Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity")
            }
    )
    @PostMapping("/{walletAddress}/withdraw")
    public ResponseEntity<WalletDto> withdraw(
            @Parameter(description = "Wallet Address for withdraw", required = true)
            @PathVariable Long walletAddress,
            @Parameter(description = "amount to be withdrawn", required = true)
            @RequestParam BigDecimal amount,
            @Parameter(description = "PSP code for transaction", required = true)
            @RequestParam String pspCode) {
        return ResponseEntity.ok(walletService.withdraw(walletAddress, amount, pspCode));
    }

    @Operation(
            summary = "Transfer Money",
            description = "Transfer money from one wallet to another wallet"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "transfer successfully",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class, description = "transaction ID"))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Wallet Not Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity")
            }
    )
    @PostMapping("/{walletAddress}/transfer/{toWalletAddress}")
    public ResponseEntity<String> transfer(
            @Parameter(description = "Wallet Address for withdraw", required = true)
            @PathVariable Long walletAddress,
            @Parameter(description = "Wallet Address to deposit", required = true)
            @PathVariable Long toWalletAddress,
            @Parameter(description = "amount to be transferred", required = true)
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.transfer(walletAddress, toWalletAddress, amount));
    }

    @Operation(
            summary = "Get Currency List",
            description = "load currency list of wallets"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "currency list loaded successfully",
                            content = {
                                    @Content(mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
                            })
            }
    )
    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencyList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.ok(walletService.getCurrencyList());
    }

    @Operation(
            summary = "Get PSP List",
            description = "load PSP list of transactions"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "PSP list loaded successfully",
                            content = {
                                    @Content(mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
                            })
            }
    )
    @GetMapping("/psp")
    public ResponseEntity<List<String>> getPSPList() {
        log.info("user register requester: '{}'", (
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.ok(walletService.getPSPList());
    }

    @Operation(
            summary = "Wallet Transaction Report",
            description = "create transaction report by wallet address and date. Coming Soon!"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "report created successfully",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),

            }
    )
    @GetMapping("/{walletAddress}/report")
    @PostAuthorize("principal.username == returnObject.body.username")
    public ResponseEntity<WalletDto> createTransactionReportByWalletAddress(
            @Parameter(description = "Wallet Address of the user to create report", required = true)
            @PathVariable Long walletAddress,
            @Parameter(description = "Lower bound of the date to create the report", required = true)
            @RequestParam LocalDateTime fromDate,
            @Parameter(description = "Upper bound of the date to create the report", required = true)
            @RequestParam LocalDateTime toDate) {
        log.info("report requester: '{}' resource address: '{}'",
                ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getUsername(), walletAddress);
        return ResponseEntity.ok().build(); // There is not enough time to develop this report feature
    }

}
