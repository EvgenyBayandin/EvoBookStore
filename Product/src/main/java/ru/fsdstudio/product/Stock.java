package ru.fsdstudio.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
}

//@Getter
//@Setter
//@Entity
//@Table(name = "stocks", uniqueConstraints = {
//        @UniqueConstraint(name = "unique_stock_product_price", columnNames = {"product_id", "price"})
//})
//public class Stock {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @Column(name = "price", precision = 19, scale = 2)
//    private BigDecimal price;
//
//    @Column(name = "quantity")
//    private Long quantity;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//}