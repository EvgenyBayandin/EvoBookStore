package ru.fsdstudio.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

/**
 * DTO for {@link Book}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
    Long id;
    String author;
}