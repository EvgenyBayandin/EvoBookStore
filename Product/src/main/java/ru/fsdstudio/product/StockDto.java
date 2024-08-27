package ru.fsdstudio.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Value;

/**
 * DTO for {@link Stock}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockDto {
    Long id;
    BigDecimal price;
    Long quantity;
}