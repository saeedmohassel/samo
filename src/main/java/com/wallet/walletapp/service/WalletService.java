package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.dto.WalletRequestDto;

import java.util.List;

public interface WalletService {

    List<String> getCurrencyList();

    List<String> getPSPList();

    WalletDto registerWallet(WalletRequestDto walletRequest);

    WalletDto findWalletByAddress(Long walletAddress);

}
