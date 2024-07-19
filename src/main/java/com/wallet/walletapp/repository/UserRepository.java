package com.wallet.walletapp.repository;

import com.wallet.walletapp.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
}
