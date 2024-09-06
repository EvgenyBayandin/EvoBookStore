package ru.fsdstudio.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.fsdstudio.product.entity.Product;

/**
 * DTO for {@link Product}
 */
@Setter
@Getter
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Long quantity;
    
    public ProductDto() {
    }
    
    public ProductDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
