package ru.fsdstudio.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for {@link Order}
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Status status;
    private Long customerId;
    private List<OrderItemDto> orderItems;
    private BigDecimal totalAmount;
    private Object[] totalQuantityAndAmount;
    
    public OrderDto() {
        this.orderItems = new ArrayList<>();
    }
}