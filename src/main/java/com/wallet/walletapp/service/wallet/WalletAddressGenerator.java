package com.wallet.walletapp.service.wallet;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class WalletAddressGenerator {

    public Long generateWalletAddress() {
        int firstDigit = ThreadLocalRandom.current().nextInt(1, 10);
        long last13Digits = System.currentTimeMillis() % 10000000000000L;
        String last13DigitsStr = String.format("%013d", last13Digits);
        String numberStr = firstDigit + last13DigitsStr;
        return Long.parseLong(numberStr);
    }

}
