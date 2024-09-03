package ru.fsdstudio.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.fsdstudio.product.entity.Book;

/**
 * DTO for {@link Book}
 */
@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
    Long id;
    String name;
    String description;
    String author;
    BigDecimal price;
    Long quantity;
}
