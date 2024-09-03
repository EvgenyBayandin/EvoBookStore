package ru.fsdstudio.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.fsdstudio.order.entity.OrderItem;

/**
 * DTO for {@link OrderItem}
 */
@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDto {
    Long id;
    Long productId;
    String productName;
    String productDescription;
    Long quantity;
    BigDecimal amount;
    
    public OrderItemDto() {}
    
    public OrderItemDto(Long id, Long productId, String productName, String productDescription, Long quantity, java.math.BigDecimal amount) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.amount = amount;
    }
}

