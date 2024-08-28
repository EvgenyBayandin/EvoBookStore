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
    private BigDecimal totalAmount;
    private List<OrderItemsDto> orderItems;
    
    public OrderDto() {
        this.orderItems = new ArrayList<>();
    }
    
    public void addOrderItem(OrderItemsDto orderItem) {
        this.orderItems.add(orderItem);
    }
    
    /**
     * DTO for {@link OrderItems}
     */
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItemsDto {
        Long id;
        Long productId;
        String productName;
        String productDescription;
        Long quantity;
        BigDecimal amount;
        
        public OrderItemsDto() {}
    }
}