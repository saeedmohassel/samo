package com.wallet.walletapp.repository;

import com.wallet.walletapp.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findWalletByAddress(Long walletAddress);

}
