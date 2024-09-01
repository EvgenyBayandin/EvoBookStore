package ru.fsdstudio.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stocks", uniqueConstraints = {
        @UniqueConstraint(name = "unique_stock_product_price", columnNames = {"product_id", "price"})
})
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;
    
    @Column(name = "quantity")
    private Long quantity;
    
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    public Stock() {
        this.price = BigDecimal.ZERO;
        this.quantity = 0L;
    }
}
