package com.wallet.walletapp.service.person;

import com.wallet.walletapp.model.dto.PersonDto;
import com.wallet.walletapp.model.dto.PersonRequestDto;
import com.wallet.walletapp.model.entity.Gender;
import com.wallet.walletapp.model.entity.Person;
import com.wallet.walletapp.model.mapper.PersonMapper;
import com.wallet.walletapp.repository.PersonRepository;
import com.wallet.walletapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonMapper personMapper;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @Override
    public PersonDto registerPerson(PersonRequestDto personRequest) {
        Person person = new Person();
        person.setUser(userRepository.findUserByUsername(
                        personRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist",
                        personRequest.getUsername())))
        );
        person.setFirstName(personRequest.getFirstName());
        person.setLastName(personRequest.getLastName());
        person.setNationalCode(personRequest.getNationalCode());
        person.setBirthdate(personRequest.getBirthdate());
        person.setGender(getGender(personRequest.getGender()));
        person.setInsertTime(LocalDateTime.now());
        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    @Override
    public PersonDto loadPersonByUsername(String username) {
        return personMapper.toDto(personRepository.findPersonByUser_Username(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username '%s' does not exist", username)))
        );
    }

    private static Gender getGender(String gender) {
        try {
            return Gender.valueOf(gender);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("gender is Not Valid!");
        }
    }

}
