package ru.fsdstudio.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.fsdstudio.person.entity.Customer;

/**
 * DTO for {@link Customer}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResponceDtoV1 {
    Long id;
    String firstName;
    String lastName;
    String patronymic;
    String email;
    String username;
    String role;
}