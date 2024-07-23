package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;
import com.wallet.walletapp.model.entity.Currency;
import com.wallet.walletapp.model.entity.PSP;
import com.wallet.walletapp.model.entity.Wallet;
import com.wallet.walletapp.model.mapper.WalletMapper;
import com.wallet.walletapp.repository.PersonRepository;
import com.wallet.walletapp.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final PersonRepository personRepository;
    private final WalletRepository walletRepository;
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
        wallet.setAddress(generateWalletAddress());

        wallet.setUser(personRepository.findPersonByUser_Username(
                        walletRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist",
                        walletRequest.getUsername())))
        );
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    private String generateWalletAddress() {
        return "ABC";
    }

}
