package com.wallet.walletapp.model.mapper;

import com.wallet.walletapp.model.dto.WalletDto;
import com.wallet.walletapp.model.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mappings({
            @Mapping(source = "insertTime", target = "registerDate"),
            @Mapping(source = "currency", target = "currencyCode"),
            @Mapping(source = "person.user.username", target = "username"),
            @Mapping(source = "name", target = "walletName"),
            @Mapping(source = "address", target = "walletAddress")
    })
    WalletDto toDto(Wallet entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "transactionList", ignore = true),
            @Mapping(target = "insertTime", ignore = true)
    })
    Wallet toEntity(WalletDto dto);

}