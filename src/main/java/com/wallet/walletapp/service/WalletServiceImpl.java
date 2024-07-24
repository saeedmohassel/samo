package com.wallet.walletapp.service;

import com.wallet.walletapp.exception.InsufficientFundException;
import com.wallet.walletapp.exception.ResourceNotFoundException;
import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;
import com.wallet.walletapp.model.entity.*;
import com.wallet.walletapp.model.mapper.WalletMapper;
import com.wallet.walletapp.repository.PersonRepository;
import com.wallet.walletapp.repository.TransactionRepository;
import com.wallet.walletapp.repository.WalletRepository;
import com.wallet.walletapp.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletAddressGenerator addressGenerator;
    private final PersonRepository personRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;

    @Override
    public List<String> getCurrencyList() {
        return Arrays.stream(Currency.values()).map(Currency::name).collect(Collectors.toList());
    }

    @Override
    public List<String> getPSPList() {
        return Arrays.stream(PSP.values()).map(PSP::name).collect(Collectors.toList());
    }

    @Override
    public WalletDto registerWallet(WalletRequestDto walletRequest) {
        Wallet wallet = new Wallet();
        wallet.setName(walletRequest.getWalletName());
        wallet.setCurrency(getCurrency(walletRequest.getCurrencyCode()));
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setAddress(addressGenerator.generateWalletAddress());

        wallet.setPerson(personRepository.findPersonByUser_Username(
                        walletRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist",
                        walletRequest.getUsername())))
        );
        wallet.setInsertTime(LocalDateTime.now());
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    private static Currency getCurrency(String currencyCode) {
        try {
            return Currency.valueOf(currencyCode);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Currency code is not Valid!");
        }
    }

    @Override
    public WalletDto findWalletByAddress(Long walletAddress) {
        return walletMapper.toDto(findWalletEntityByAddress(walletAddress));
    }

    private Wallet findWalletEntityByAddress(Long walletAddress) {
        return walletRepository.findWalletByAddress(walletAddress)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("wallet with address '%s' does not exist", walletAddress)));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public WalletDto deposit(Long walletAddress, BigDecimal amount, String pspCode) {
        Wallet wallet = findWalletEntityByAddress(walletAddress);
        checkRequesterAccess(wallet.getPerson().getUser().getUsername());
        String transferId = UUID.randomUUID().toString();

        Transaction transaction = createTransaction(
                wallet, amount, TransactionType.DEPOSIT,
                1D, getPspCode(pspCode), transferId);
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().add(amount));
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public WalletDto withdraw(Long walletAddress, BigDecimal amount, String pspCode) {
        Wallet wallet = findWalletEntityByAddress(walletAddress);
        checkRequesterAccess(wallet.getPerson().getUser().getUsername());
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundException("Insufficient Funds");
        }

        String transferId = UUID.randomUUID().toString();

        Transaction transaction = createTransaction(
                wallet, amount, TransactionType.WITHDRAW,
                1D, getPspCode(pspCode), transferId);
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    private static PSP getPspCode(String pspCode) {
        try {
            return PSP.valueOf(pspCode);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("PSP code is Not Valid!");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public String transfer(Long fromWalletAddress, Long toWalletAddress, BigDecimal amount) {
        Wallet fromWallet = findWalletEntityByAddress(fromWalletAddress);
        checkRequesterAccess(fromWallet.getPerson().getUser().getUsername());

        Wallet toWallet = findWalletEntityByAddress(toWalletAddress);

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundException("Insufficient Funds");
        }

        String transferId = UUID.randomUUID().toString();

        Transaction fromTransaction = createTransaction(
                fromWallet, amount, TransactionType.TRANSFER_WITHDRAW,
                1D, PSP.WALLET, transferId);
        transactionRepository.save(fromTransaction);

        Transaction toTransaction = createTransaction(
                toWallet, amount, TransactionType.TRANSFER_DEPOSIT,
                1D, PSP.WALLET, transferId);
        transactionRepository.save(toTransaction);

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        walletRepository.save(fromWallet);

        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(toWallet);
        return transferId;
    }

    private static Transaction createTransaction(Wallet wallet, BigDecimal amount, TransactionType deposit,
                                                 Double exchangeRate, PSP pspCode, String transferId) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setCurrency(wallet.getCurrency());
        transaction.setExchangeRate(exchangeRate);
        transaction.setType(deposit);
        transaction.setPsp(pspCode);
        transaction.setInsertTime(LocalDateTime.now());
        transaction.setTransactionUUID(transferId);
        return transaction;
    }

    private void checkRequesterAccess(String username) {
        if (!((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername().equals(username)) {
            throw new AccessDeniedException("Access Denied");
        }
    }

}
