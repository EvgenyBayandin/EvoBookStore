package ru.fsdstudio.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

/**
 * DTO for {@link Product}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    Long id;
    String name;
    String description;
}