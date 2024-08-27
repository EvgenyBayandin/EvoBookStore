package ru.fsdstudio.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

/**
 * DTO for {@link Customer}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequestDtoV1 {
    Long id;
    String firstName;
    String lastName;
    String patronymic;
    String email;
    String username;
    String password;
    String role;
}