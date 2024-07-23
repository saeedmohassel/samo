package com.wallet.walletapp.service;

import com.wallet.walletapp.model.dto.PersonDto;
import com.wallet.walletapp.model.dto.PersonRequestDto;
import com.wallet.walletapp.model.entity.Gender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    @Override
    public PersonDto registerPerson(PersonRequestDto personRequest) {
        return null;
    }

    @Override
    public List<String> getGenders() {
        return Arrays.stream(Gender.values()).map(Gender::name).collect(Collectors.toList());
    }


}
