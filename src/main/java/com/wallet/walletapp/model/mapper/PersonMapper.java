package com.wallet.walletapp.model.mapper;

import com.wallet.walletapp.model.dto.PersonDto;
import com.wallet.walletapp.model.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = WalletMapper.class)
public interface PersonMapper {

    PersonDto toDto(Person entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "insertTime", ignore = true),
    })
    Person toEntity(PersonDto dto);

}