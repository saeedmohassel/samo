package com.wallet.walletapp.model.mapper;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletDto toDto(Wallet entity);

    Wallet toEntity(WalletDto dto);

}