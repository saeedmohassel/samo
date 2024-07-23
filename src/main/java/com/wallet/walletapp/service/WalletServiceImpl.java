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
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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
        wallet.setCurrency(Currency.valueOf(walletRequest.getCurrencyCode()));
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setAddress(addressGenerator.generateWalletAddress());

        wallet.setUser(personRepository.findPersonByUser_Username(
                        walletRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist",
                        walletRequest.getUsername())))
        );
        wallet.setInsertTime(LocalDateTime.now());
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Override
    public WalletDto findWalletByAddress(Long walletAddress) {
        return walletMapper.toDto(findWalletEntityByAddress(walletAddress)
        );
    }

    private Wallet findWalletEntityByAddress(Long walletAddress) {
        return walletRepository.findWalletByAddress(walletAddress)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("wallet with address '%s' does not exist", walletAddress)));
    }


    //------------------------------------------------------------------------------------------

    @Transactional
    @Override
    public WalletDto deposit(Long walletAddress, BigDecimal amount, String pspCode) {
        Wallet wallet = findWalletEntityByAddress(walletAddress);

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setCurrency(wallet.getCurrency());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setPsp(PSP.valueOf(pspCode));
        transaction.setInsertTime(LocalDateTime.now());
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().add(amount));
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Transactional
    @Override
    public WalletDto withdraw(Long walletAddress, BigDecimal amount, String pspCode) {
        Wallet wallet = findWalletEntityByAddress(walletAddress);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundException("Insufficient Funds");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setCurrency(wallet.getCurrency());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setPsp(PSP.valueOf(pspCode));
        transaction.setInsertTime(LocalDateTime.now());
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Transactional
    @Override
    public String transfer(Long fromWalletAddress, Long toWalletAddress, BigDecimal amount) {
        Wallet fromWallet = findWalletEntityByAddress(fromWalletAddress);
        Wallet toWallet = findWalletEntityByAddress(toWalletAddress);

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundException("Insufficient Funds");
        }

        String transferId = UUID.randomUUID().toString();

        Transaction fromTransaction = new Transaction();
        fromTransaction.setWallet(fromWallet);
        fromTransaction.setAmount(amount);
        fromTransaction.setCurrency(fromWallet.getCurrency());
        fromTransaction.setExchangeRate(1D);
        fromTransaction.setType(TransactionType.TRANSFER_WITHDRAW);
        fromTransaction.setInsertTime(LocalDateTime.now());
        fromTransaction.setTransactionUUID(transferId);
        transactionRepository.save(fromTransaction);

        Transaction toTransaction = new Transaction();
        toTransaction.setWallet(toWallet);
        toTransaction.setAmount(amount);
        toTransaction.setCurrency(toWallet.getCurrency());
        toTransaction.setExchangeRate(1D);
        toTransaction.setType(TransactionType.TRANSFER_DEPOSIT);
        toTransaction.setInsertTime(LocalDateTime.now());
        toTransaction.setTransactionUUID(transferId);
        transactionRepository.save(toTransaction);

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        walletRepository.save(fromWallet);

        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(toWallet);
        return transferId;
    }

}
