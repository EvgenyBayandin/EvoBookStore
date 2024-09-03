package ru.fsdstudio.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Value;
import ru.fsdstudio.product.entity.Stock;

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