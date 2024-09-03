package ru.fsdstudio.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.fsdstudio.person.entity.Customer;

/**
 * DTO for {@link Customer}
 */
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDto {
    Long id;
    String firstName;
    String lastName;
    String patronymic;
    String email;
    
    public CustomerDto(Long id, String firstName, String lastName, String patronymic, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.email = email;
    }
}