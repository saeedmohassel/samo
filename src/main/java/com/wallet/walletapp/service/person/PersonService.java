package com.wallet.walletapp.service.person;

import com.wallet.walletapp.model.dto.PersonDto;
import com.wallet.walletapp.model.dto.PersonRequestDto;

public interface PersonService {

    PersonDto registerPerson(PersonRequestDto personRequest);

    PersonDto loadPersonByUsername(String username);

}
