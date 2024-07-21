package com.wallet.walletapp.model.mapper;

import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(AppUser entity);

    AppUser toEntity(UserDto dto);

}