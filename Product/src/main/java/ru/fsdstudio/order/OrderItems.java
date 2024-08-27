package ru.fsdstudio.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import ru.fsdstudio.product.Product;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "product_quantity")
    private Long quantity;
    
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}