package com.wallet.walletapp.repository;

import com.wallet.walletapp.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @PostAuthorize("!returnObject.present || principal.username == returnObject.get().person.user.username")
    Optional<Wallet> findWalletByAddress(Long walletAddress);

    Optional<Wallet> findDestinationWalletByAddress(Long walletAddress);

}
